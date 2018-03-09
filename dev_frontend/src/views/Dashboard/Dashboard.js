import React, { Component } from 'react';
import ReactDataGrid   from 'react-data-grid';
import {
  Badge,
  Row,
  Col,
  FormGroup,
  Card,
  CardHeader,
  CardBody,
  CardFooter,
  CardTitle,
  Button,
  ButtonToolbar,
  ButtonGroup,
  ButtonDropdown,
  Label,
  Input,
  Table
} from 'reactstrap';
import fetch from 'node-fetch';
import  FormData from 'form-data';
import moment from 'moment';
import PropTypes from 'prop-types';

var self;
var style = {
	"textAlign" : 'right'
};

class RowRenderer extends React.Component {
  constructor(props) {
    super(props)
    var propTypes = {
      idx: PropTypes.string.isRequired
    };
    this.state = {
      propTypes : propTypes
    }
  }

  getRowStyle(row) {
    return {
      color: this.props.value === "Invalid" ? 'red' : 'green'
    };
  };

  render() {
    return ( <span style={this.getRowStyle()}>{this.props.value}</span>);
  }
}

class Dashboard extends React.Component {
	constructor(props, context) {
	  super(props, context);

   // this.createRows = this.createRows.bind(this);
	  self = this;
    this._columns = [
      { key: 'infNFeId', name: 'XML' , resizable: true  },
      { key: 'nNF', name: 'NF' ,resizable: true},
      { key: 'dEmi', name: 'EMISSAO' ,resizable: true },
      { key: 'xNome', name: 'EMITENTE',resizable: true },
      { key: 'status', name: 'status' ,resizable: true , formatter : RowRenderer} ];
		this._rows = [];
    this.state ={
      file:null,
      errorMessage : null
    };

  }

  createRows (data) {
    let rows = [];
    for (let i = 0; i < data.length; i++) {
      rows.push({
      	infNFeId : data[i].infNFeId,
      	nNF : data[i].nNF,
      	dEmi : data[i].dEmi ? moment(data[i].dEmi).format('L'): "",
      	xNome : data[i].xNome,
      	status : data[i].status
      });
    }
   
    this._rows = rows;
    this.setState({rows: rows });
  };

  rowGetter(i) {
    return self._rows[i];
  };



  onChange(e) {
  	const formData = new FormData();
		formData.append('file',e.target.files[0]);
		fetch('http://localhost:7070/upload', {
		    method: 'POST',
		    body: formData
		}).then(function(res1) {
      if (!res1.ok) {
        self.createRows([]);
        if (error.message) {
          self.setState({errorMessage :error.message});
        } 
      }
      return res1.json();
    }).then(function(response){
      console.log(response);
    	self.createRows(response.data);
    });
  }

  onChange1 (e) {
    const formData = new FormData();
    formData.append('file',e.target.files[0]);
    fetch('http://localhost:7070/validatesignature', {
        method: 'POST',
        body: formData
    }).then(function(res1) {
        return res1.json();
    }).then(function(response){
      self.createRows(response.data)
    }).catch((error) => {
      console.log(error);
    });
  }
  render() {

    return (
      <div className="animated fadeIn">
        <FormGroup row>
          <Col md="5" style={style}>
            <Label htmlFor="file-input">SELECIONE XML DA NFE : </Label>
          </Col>
          <Col xs="12" md="7">
            <Input type="file" id="file-input" name="file-input" onChange={this.onChange}/>
          </Col>
        </FormGroup>
        <Row>
          <span>Here{this.errorMessage}</span>
        </Row>
      	<Row>
	        <ReactDataGrid
	        	columns={this._columns}
	        	rowGetter={this.rowGetter}
	        	rowsCount={this._rows.length}
	        	minHeight={500} />
        </Row>
        <FormGroup>	
        </FormGroup> 
        <FormGroup row>
        	<Col md="10" style={style}>
        	</Col>
          <Col md="2" style={style}>
        		<Input type="button" id="file-input" value="IMPORTAR"/>
         	</Col>
        </FormGroup> 	
      </div>
    );
  }
}

export default Dashboard;
