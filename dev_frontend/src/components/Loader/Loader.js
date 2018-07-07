import React, {Component} from 'react';

class Loader extends Component {
	constructor(props) {
    super(props);
    this.state = {
    	action : 'none'
    }	
    console.log(this.props);
    this.show = this.show.bind(this);
  }
  

  show() {
     this.setState({action : ''});
  }

  hide() {
		this.setState({action : 'none'});
  }

  render() {
    return (
      <div className="loader" style={{display:this.state.action}}>
        <p className="loader-data">Loading... </p>
      </div>
    )
  }
}

export default Loader;
