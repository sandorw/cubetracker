import React, {Component} from 'react';
import {render} from 'react-dom';
import {Router, Route, IndexRoute, hashHistory} from 'react-router';
import {Nav, Navbar, NavItem} from 'react-bootstrap';
import {LinkContainer, IndexLinkContainer} from 'react-router-bootstrap';
import 'bootstrap/less/bootstrap.less';

import Blog from './Blog';
import List from './List';
import Cards from './Cards';
import Decks from './Decks';
import Draft from './Draft';

var App = React.createClass({
  getInitialState: function() {
    return {selectedTab: 1};
  },
  onSelect: function(selectedTab) {
    this.setState({selectedTab: selectedTab});
  },
  render: function() {
    return (
      <div>
        <Navbar>
          <Navbar.Header>
            <Navbar.Brand>
              <a href="#">Cubetracker</a>
            </Navbar.Brand>
            <Navbar.Toggle />
          </Navbar.Header>
          <Navbar.Collapse>
            <Nav bsStyle="tabs" activeKey={this.state.selectedKey} onSelect={this.onSelect}>
              <IndexLinkContainer to="/">
                <NavItem eventKey={1}>Blog</NavItem>
              </IndexLinkContainer>
              <LinkContainer to="/list">
                <NavItem eventKey={2}>List</NavItem>
              </LinkContainer>
              <LinkContainer to="/cards">
                <NavItem eventKey={3}>Cards</NavItem>
              </LinkContainer>
              <LinkContainer to="/decks">
                <NavItem eventKey={4}>Decks</NavItem>
              </LinkContainer>
              <LinkContainer to="/draft">
                <NavItem eventKey={5}>Draft</NavItem>
              </LinkContainer>
            </Nav>
          </Navbar.Collapse>
        </Navbar>        
        <div className="content">
          {this.props.children}
        </div>
      </div>
    )
  }
});

render((
  <Router history={hashHistory}>
    <Route path="/" component={App}>
      <IndexRoute component={Blog}/>
      <Route path="list" component={List}/>
      <Route path="cards" component={Cards}/>
      <Route path="decks" component={Decks}/>
      <Route path="draft" component={Draft}/>
    </Route>
  </Router>
), document.getElementById('app'));
