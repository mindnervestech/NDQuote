import config from '../config'; 
import fetch from 'node-fetch';
const XMLDocumentService = {
  getDocumentData(userId) {
  	return new Promise((resolve, reject) =>{
  		fetch( config.baseURL + 'ndquote/api/document/'+userId , {
        method: 'GET',
        headers: { 'X-AUTH-TOKEN' : localStorage.getItem('token')}
      }).then(function(res1) {
	      if (!res1.ok) {
	         reject(res1.json());
	      } else {
	      	resolve(res1.json());
	      }
	    });
	  });     
  },
	documentUpload(userId,file) {
		return new Promise((resolve, reject) =>{
			const formData = new FormData();
		  formData.append('file',file);
			fetch( config.baseURL + 'ndquote/api/1/upload', {
		    method: 'POST',
		    body: formData,
        headers: { 'X-AUTH-TOKEN' : localStorage.getItem('token')}
		 }).then(function(res1) {
	      if (!res1.ok) {
	         reject(res1.json());
	      } else {
	      	resolve(res1.json());
	      }
		  });
	  });
	}

};

export default XMLDocumentService;
