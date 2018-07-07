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
import  FormData from 'form-data';
import moment from 'moment';
import PropTypes from 'prop-types';
import XMLDocumentService from '../../Services/XMLDocumentService';
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

class AcaoRowRenderer extends React.Component {
  constructor(props) {
    super(props)
    var propTypes = {
      idx: PropTypes.string.isRequired
    };
    this.state = {
      propTypes : propTypes
    }
    this.onItemClick =this.onItemClick.bind(this);
  }

  onItemClick() {
    console.log("this.props :: ", this.props.value)
    if (this.props.value && this.props.value.id)
      self.props.history.push('/tokens/'+this.props.value.id);
  }

  getButtonStyle(data) {
    if (this.props.value.id ) {
      if ( data === "import") {
        return {
          display: this.props.value && this.props.value.authorised  ? '' : 'none'
        };
      }  else {
        return {
          cursor : 'pointer',
          margin: '0px auto',
          display: this.props.value && this.props.value.authorised  ? 'none' : ''
        };
      } 
    } else {
      return {
          display:  'none' 
        };
    }
  };
  render() {
    return ( 
      <Row>
       
        <Col md="12" style={{textAlign:"center"}}>
          <i className="fa fa-gear fa-lg" onClick={this.onItemClick} style={this.getButtonStyle('gerar')}></i>
            <svg version="1.0" xmlns="http://www.w3.org/2000/svg"
               width="20px" height="20px" viewBox="0 0 128.000000 128.000000"
               preserveAspectRatio="xMidYMid meet"  style={this.getButtonStyle('import')}>
              <metadata>
              Created by potrace 1.15, written by Peter Selinger 2001-2017
              </metadata>
              <g transform="translate(0.000000,128.000000) scale(0.100000,-0.100000)"
              fill="#000000" stroke="none">
              <path d="M606 1264 c-3 -9 -6 -167 -6 -352 l0 -336 -68 67 c-37 37 -74 67 -83
              67 -20 0 -39 -20 -39 -40 0 -26 203 -220 230 -220 27 0 230 194 230 220 0 20
              -19 40 -39 40 -9 0 -46 -30 -83 -67 l-68 -67 0 336 c0 185 -3 343 -6 352 -4 9
              -18 16 -34 16 -16 0 -30 -7 -34 -16z"/>
              <path d="M167 893 c-4 -3 -7 -206 -7 -450 l0 -443 480 0 480 0 -2 448 -3 447
              -157 3 -158 3 0 -36 0 -35 125 0 125 0 0 -380 0 -380 -410 0 -410 0 0 380 0
              380 125 0 125 0 0 35 0 35 -153 0 c-85 0 -157 -3 -160 -7z"/>
              </g>
            </svg>
        </Col>
      </Row>
    );
  }
}


class Dashboard extends React.Component {
	constructor(props, context) {
	  super(props, context);
    if (!localStorage.getItem('userId') ) {
      props.history.push('/login');
    }
   // this.createRows = this.createRows.bind(this);
	  self = this;
    this._columns = [
      { key: 'infNFeId', name: 'Chave da NFe' , resizable: true  },
      { key: 'nNF', name: 'NFe' ,resizable: true ,width : 80},
      { key: 'dEmi', name: 'Emissão' ,resizable: true ,width : 130},
      { key: 'xNome', name: 'Emitente',resizable: true },
      { key: 'status', name: 'Status' ,resizable: true , formatter : RowRenderer,width : 80},
      { key: 'authorised' , name: 'Ação', resizable: true,width : 140,formatter : AcaoRowRenderer } ];
		this._rows = [];
    this.state ={
      file:null,
      errorMessage : null,
      userId : localStorage.getItem('userId')
    }; 
    this.loadData();
  }
  loadData() {
    XMLDocumentService.getDocumentData( self.state.userId ).then(response=>{
      self.createRows(response.data);
    }).catch(error=>{
      console.log(error);
      self.createRows([]);
    }); 
  }
  createRows (data) {
    let rows = [];
    for (let i = 0; i < data.length; i++) {
      rows.push({
      	infNFeId : data[i].infNFeId,
      	nNF : data[i].nNF,
      	dEmi : data[i].dEmi ? moment(data[i].dEmi).format('DD/MM/YYYY HH:mm'): "",
      	xNome : data[i].xNome,
      	status : data[i].status,
        authorised:data[i]
      });
    }
    this._rows = rows;
    this.setState({rows: rows });
  };

  rowGetter(i) {
    return self._rows[i];
  };
  
  onChange(e) {
    XMLDocumentService.documentUpload( self.state.userId ,e.target.files[0]).then(response=>{
      self.createRows(response.data);
    }).catch(error=>{
      console.log(error);
      self.createRows([]);
    }); 
  }

  render() {
    return (
      <div className="animated fadeIn">
        <Row>
            <Label htmlFor="file-input">Selecione XML : </Label>
            <Col  style={style}>
              <Input type="file" id="file-input" name="file-input"  onChange={this.onChange}/>
            </Col>
        </Row>
        <Row>
          <span>{this.errorMessage}</span>
        </Row>
        <FormGroup> 
        </FormGroup> 
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
