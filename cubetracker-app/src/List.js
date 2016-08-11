import React from 'react';
import {render} from 'react-dom';
import $ from 'jquery';
import {ListGroup, ListGroupItem} from 'react-bootstrap';

export default class List extends React.Component {
  constructor(props) {
    super(props);
    this.state = {selectedCard: ''};
    this.selectCard = this.selectCard.bind(this);
  }
  selectCard(selectedCard) {
    this.setState({selectedCard: selectedCard});
  }
  render() {
    return (
      <div className="twoWaySplit">
        <div className="leftComponent">
          <CubeCards onSelect={this.selectCard} />
        </div>
        <div className="rightComponent">
          <CardInfo name={this.state.selectedCard} />
        </div>
      </div>
    )
  }
}

class CardInfo extends React.Component {
  constructor(props) {
    super(props);
    this.state = {cardusage: {}};
    this.loadCardUsage = this.loadCardUsage.bind(this);
  }
  loadCardUsage(cardname) {
    var url = "http://localhost:8080/cubecards/" + cardname;
    var activeRequest = $.ajax({
      type: 'GET',
      url: url,
      success: function(cardusage) {
        this.setState({cardusage: cardusage});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(url, status, err.toString());
      }.bind(this)
    });
    this.setState({activeRequest: activeRequest});
  }
  componentWillUnmount() {
    this.state.activeRequest.abort();
  }
  componentWillReceiveProps(nextProps) {
    this.loadCardUsage(nextProps.name);
  }
  render() {
    return (
      <div>
        {$.isEmptyObject(this.state.cardusage) ?
          <p>No card selected</p> :  
          <div>
            <p>Card name: {this.props.name}</p>
            <p>Status: {this.state.cardusage.isActive ? "Active" : "Inactive"}</p>
            <p>Number of wins: {this.state.cardusage.numWins}</p>
            <p>Number of losses: {this.state.cardusage.numLosses}</p>
            <p>Number of drafts: {this.state.cardusage.numDrafts}</p>
            <p>Number of maindecks: {this.state.cardusage.numMaindecks}</p>
          </div>
        }
      </div>
    )
  }
}

class CubeCards extends React.Component {
  constructor(props) {
    super(props);
    this.state = {active: [], inactive: []};
    this.loadCardLists = this.loadCardLists.bind(this);
    this.activeSubmit = this.activeSubmit.bind(this);
    this.inactiveSubmit = this.inactiveSubmit.bind(this);
  }
  loadCardLists() {
    var activeRequest = $.ajax({
      type: 'GET',
      url: "http://localhost:8080/cubecards/active",
      success: function(cardlist) {
        this.setState({active: cardlist});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(url, status, err.toString());
      }.bind(this)
    });
    this.setState({activeRequest: activeRequest});
    var inactiveRequest = $.ajax({
      type: 'GET',
      url: "http://localhost:8080/cubecards/inactive",
      success: function(cardlist) {
        this.setState({inactive: cardlist});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(url, status, err.toString());
      }.bind(this)
    });
    this.setState({inactiveRequest: inactiveRequest});
  }
  activeSubmit(cardrequest) {
    var cardname = cardrequest.cardname;
    var url = "http://localhost:8080/cubecards/active/" + cardname;
    $.ajax({
      type: 'PUT',
      url: url,
      success: function(carddata) {},
      error: function(xhr, status, err) {
        console.error(url, status, err.toString());
      }.bind(this)
    });
  }
  inactiveSubmit(cardrequest) {
    var cardname = cardrequest.cardname;
    var url = "http://localhost:8080/cubecards/inactive/" + cardname;
    $.ajax({
      type: 'PUT',
      url: url,
      success: function(carddata) {},
      error: function(xhr, status, err) {
        console.error(url, status, err.toString());
      }.bind(this)
    });
  }
  componentDidMount() {
    this.loadCardLists();
    var intervalId = setInterval(this.loadCardLists, 1000);
    this.setState({intervalId: intervalId});
  }
  componentWillUnmount() {
    clearInterval(this.state.intervalId);
    this.state.activeRequest.abort();
    this.state.inactiveRequest.abort();
  }
  render() {
    return (
      <div>
        <CardList list={this.state.active} onSelect={this.props.onSelect} />
        <CardSelector label="Add active card" onSubmit={this.activeSubmit} />
        <CardList list={this.state.inactive} onSelect={this.props.onSelect} />
        <CardSelector label="Add inactive card" onSubmit={this.inactiveSubmit} />
      </div>
    )
  }
}

class CardList extends React.Component {
  render() {
    return (
      <ListGroup>
        {this.props.list.map(function(name) {
            return <ListGroupItem onClick={this.props.onSelect.bind(this, name)}>{name}</ListGroupItem>;
        }, this)}
      </ListGroup>
    )
  }
}

class CardSelector extends React.Component {
  constructor(props) {
    super(props);
    this.state = {text: ''};
    this.handleTextChange = this.handleTextChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }
  handleTextChange(e) {
    this.setState({text: e.target.value});
  }
  handleSubmit(e) {
    e.preventDefault();
    var cardname = this.state.text.trim();
    if (!cardname) {
      return;
    }
    this.props.onSubmit({cardname: cardname});
    this.setState({text: ''});
  }
  render() {
    return (
      <div>
        <form className="cardSelector" onSubmit={this.handleSubmit}>
          <input type="text" placeholder="Card name here" value={this.state.text} onChange={this.handleTextChange} />
          <input type="submit" value={this.props.label} />
        </form>
      </div>
    )
  }
}
