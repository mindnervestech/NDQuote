import React, {Component} from 'react';
import {Container, Row, Col, CardGroup, Card, CardBody, Button, Input, InputGroup, InputGroupAddon, InputGroupText} from 'reactstrap';
import fetch from 'node-fetch';
import FacebookLogin from 'react-facebook-login';
import GoogleLogin from 'react-google-login';

class Login extends Component {
  constructor(props) {
    super(props);
    this.state = {
      username: "",
      password:"",
      //baseUrl : 'http://localhost:7070/',
      baseURL : 'http://45.33.31.20:7070/',
      
    };
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.responseGoogle =this.responseGoogle.bind(this);
  }

  handleChange (event) {
    let data = {};
    data[event.target.name] =event.target.value;
    this.setState(data);
  }

  onLogin () {

    console.log(" Login :: ", this.props ); 
    this.props.history.push('/dashboard');
  }

  handleSubmit(event) {
    let self = this;
    event.preventDefault();
    let data =  {
      username: this.state.username,
      password: this.state.password,
    };
    fetch( self.state.baseURL + 'user/login', {
        method: 'POST',
        body: JSON.stringify(data),
        headers: { 'Content-Type': 'application/json' }
    }).then(function(res1) {
      if (!res1.ok) {
        if (error.message) {
          self.setState({ errorMessage :error.message });
        } 
      }
      return res1.json();
    }).then(function(response){
      localStorage.setItem('token' ,response.token);
      localStorage.setItem('userId' ,response.userId); 
      self.loadUserInfo(response.userId);
      self.props.history.push('/dashboard');
    });
  }

  responseFacebook (response) {
    console.log(response);
    //this.signup(response, 'facebook');
  }

  responseFacebookFail (response) {
    console.log(response);
    //this.signup(response, 'facebook');
  }
  
  responseGoogle (response) {
    let self = this;
    console.log("Google console");
    console.log(response);
    let data = {
      provider : "google",
      providerId : response.profileObj.googleId,
      userName : response.profileObj.googleId,
      profilePic : response.profileObj.imageUrl,
      firstName : response.profileObj.givenName,
      lastName : response.profileObj.familyName,
      email : response.profileObj.email
    };
    fetch( self.state.baseURL + 'user/sociallogin', {
        method: 'POST',
        body: JSON.stringify(data),
        headers: { 'Content-Type': 'application/json' }
    }).then(function(res1) {
      if (!res1.ok) {
        if (error.message) {
         
        } 
      }
      return res1.json();
    }).then(function(response){
      localStorage.setItem('token' ,response.token);
      localStorage.setItem('userId' ,response.userId); 
      self.props.history.push('/dashboard');
      console.log(response);
    });
    
    //this.signup(response, 'facebook');
  }
  
  loadUserInfo (id) {
    fetch( self.state.baseURL + 'ndquote/api/profile/'+ id, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' ,'X-AUTH-TOKEN' : localStorage.getItem('token')}
    }).then(function(res1) {
      if (!res1.ok) {
        if (error.message) {
         
        } 
      }
      return res1.json();
    }).then(function(response){
      console.log(response);
    });
  }

  render() {
    return (
      <div className="app flex-row align-items-center">
        <Container>
          <Row className="justify-content-center">
            <Col md="8">
              <CardGroup>
                <Card className="p-4">
                  <CardBody>
                    <h1>Login</h1>
                    <p className="text-muted">Sign In to your account</p>
                    <form onSubmit={this.handleSubmit}>
                    <InputGroup className="mb-3">
                      <InputGroupAddon addonType="prepend">
                        <InputGroupText>
                          <i className="icon-user"></i>
                        </InputGroupText>
                      </InputGroupAddon>
                      <Input type="text" name="username" placeholder="Username" value={this.state.username} onChange={this.handleChange} required/>
                    </InputGroup>
                    <InputGroup className="mb-4">
                      <InputGroupAddon addonType="prepend">
                        <InputGroupText>
                          <i className="icon-lock"></i>
                        </InputGroupText>
                      </InputGroupAddon>
                      <Input type="password" name="password" value={this.state.password} placeholder="Password" onChange={this.handleChange} required/>
                    </InputGroup>
                    <Row>
                      <Col xs="6">
                        <Button color="primary" className="px-4" >Login</Button>
                      </Col>
                      <Col xs="6" className="text-right">
                        <Button color="link" className="px-0">Forgot password?</Button>
                      </Col>
                    </Row>
                    </form>
                  </CardBody>
                </Card>
                <Card className="text-white bg-primary py-5 d-md-down-none" style={{ width: 44 + '%' }}>
                  <CardBody className="text-center">
                    <div>
                      <h2>Login With</h2>
                      <p></p>
                      <FacebookLogin
                      appId="350293272148788"
                      autoLoad={false}
                      cssClass="lg btn-facebook"
                      textButton="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Facebook&nbsp;&nbsp;&nbsp;"
                      fields="email"
                      callback={this.responseFacebook}
                      onFailure={this.responseFacebookFail}/>
                      <br/><br/>
                      <GoogleLogin
                      clientId="933999777984-hcp32pl0ukgii9d2q1eup4b7cva7veur.apps.googleusercontent.com"
                      buttonText="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Google+&nbsp;&nbsp;&nbsp;&nbsp;"
                      className="lg btn-google-plus"
                      onSuccess={this.responseGoogle}
                      onFailure={this.responseGoogle}/>
                    </div>
                  </CardBody>
                </Card>
              </CardGroup>
            </Col>
          </Row>
        </Container>
      </div>
    );
  }
}

export default Login;

///*<Button color="primary" className="mt-3" active>Register Now!</Button>*/