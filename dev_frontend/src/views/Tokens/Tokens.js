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
import update from 'immutability-helper';
import TokenService from '../../Services/TokenService';
var self;
var style = {
	"textAlign" : 'right'
};

class RowRenderer extends React.Component {
  constructor(props) {
    super(props)
  }
  render() {
    return (
      <Row> 
        <Col md="12" style={{textAlign:"center"}}>
          <input type="checkbox" checked={this.props.value|| false } readOnly="true" />
        </Col>
      </Row> 
    );
  }
}

class Tokens extends React.Component {
	constructor(props, context) {
	  super(props, context);
    if (!localStorage.getItem('userId') ) {
      props.history.push('/login');
    }
    self = this;
    this._columns = [
      { key: 'id', name: 'ID' , resizable: true,width : 80  },
      { key: 'document', name: 'Documento' ,resizable: true},
      { key: 'recipient', name: 'Destinatario' ,resizable: true },
      { key: 'date', name: 'Date e Hara',resizable: true },
      { key: 'token', 
        name: 'Token' ,
        resizable: true ,
        width : 180, 
        editable: function(rowData) {
          return !rowData.authorised ;
        }
      },
      { key: 'email', name: 'E-Mail' ,resizable: true ,width : 100},
      { key: 'authorised', name: 'Autorizado' ,resizable: true , formatter : RowRenderer ,width : 100} ];
		this._rows = [];
    this.state ={
      file:null,
      errorMessage : null,
      baseURL : 'http://localhost:7070/',
      //baseURL : 'http://45.33.31.20:7070/'
      rows : [],
      userId : localStorage.getItem('userId')
    };

    var params = this.props.location.pathname.split("/");
    if(params.length > 2 ) {
      this.getSingleToken(params[2]);
    } else {
      this.loadData(null);
    }
   
    this.handleGridRowsUpdated=this.handleGridRowsUpdated.bind(this);
    this.rowGetter = this.rowGetter.bind(this);
    this.checkFlag = this.checkFlag.bind(this);
  }

  createRows (data) {
    let rows = [];
    for (let i = 0; i < data.length; i++) {
      rows.push({
      	id : i+1,
        tokenId : data[i].id,
      	document : data[i].document,
        recipient :data[i].recipient,
      	date : data[i].createdAt ? moment(data[i].createdAt).format('DD/MM/YYYY HH:mm'): "",
      	token : data[i].authorised ?data[i].token : '' ,
      	email : data[i].email,
        authorised: data[i].authorised
      });
    }
    this.setState({rows: rows });
  };

  rowGetter(i) {
    return self.state.rows[i];
  };
  
  loadData() {
    TokenService.getTokenListData(self.state.userId).then(response=> {
      self.createRows(response.data);
    }).catch(error=>{
      console.log("error :: ", error);
      self.createRows([]);
    });
  }

  getSingleToken(tokenId) {
    TokenService.getSingleToken(self.state.userId,tokenId).then(response=> {
      self.createRows(response.data);
    }).catch(error=>{
      console.log("error :: ", error);
      self.createRows([]);
    });
  }


  checkFlag(index, tokenData, tokenString) {
    TokenService.checkTokenFlag(tokenData.tokenId,tokenString).then(response=> {
      var authorised = false;
      if(response.message === 'Valid') {
        authorised = true;
      }
      let rows = self.state.rows.slice();
      let rowToUpdate = rows[index];
      let updatedRow = update(rowToUpdate, {$merge: {authorised:authorised}});
      rows[index] = updatedRow;
      self.setState({ rows :rows });
    }).catch(error=>{
      console.log("error :: ", error);
    });
  }

  handleGridRowsUpdated({ fromRow, toRow, updated }) {
    let rows = this.state.rows.slice();
    for (let i = fromRow; i <= toRow; i++) {
      let rowToUpdate = rows[i];
      let updatedRow = update(rowToUpdate, {$merge: updated});
      rows[i] = updatedRow;
      if (updated.token && updated.token != '' ) {
        this.checkFlag( i,rows[i],updated.token);
      }
    }
    this.setState({ rows :rows });
  };
  render() {
    
    return (
      <div className="animated fadeIn">
      	<Row>
	        <ReactDataGrid
            enableCellSelect={true}
	        	columns={this._columns}
	        	rowGetter={this.rowGetter}
	        	rowsCount={this.state.rows.length}
	        	minHeight={600} 
            onGridRowsUpdated={this.handleGridRowsUpdated}  />
        </Row>
      </div>
    );
  }
}

export default Tokens;
