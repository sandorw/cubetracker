import React from 'react';
import {render} from 'react-dom';
import $ from 'jquery';

export default class List extends React.Component {
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
        <CardList list={this.state.active} />
        <CardSelector label="Add active card" onSubmit={this.activeSubmit} />
        <CardList list={this.state.inactive} />
        <CardSelector label="Add inactive card" onSubmit={this.inactiveSubmit} />
      </div>
    )
  }
}

class CardList extends React.Component {
  render() {
    return (
      <ul>
        {this.props.list.map(function(name){
          var link = "http://localhost:8080/cubecards/" + name;
          return <li><a href={link}>{name}</a></li>;
        })}
      </ul>
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
