import config from '../config'; 
import fetch from 'node-fetch';
const LoginService = {
  login(data) {
  	return new Promise((resolve, reject) =>{
			fetch( config.baseURL + 'user/login', {
	        method: 'POST',
	        body: JSON.stringify(data),
	        headers: { 'Content-Type': 'application/json' }
	    }).then(function(res1) {
	      if (!res1.ok) {
	         reject(res1.json());
	      } else {
	      	resolve(res1.json());
	      }
	    });
	  });     
  },
	loadUserInfo(userId) {
		return new Promise((resolve, reject) =>{
			fetch( self.state.baseURL + 'ndquote/api/profile/'+ userId, {
	      method: 'GET',
	      headers: { 'Content-Type': 'application/json' ,'X-AUTH-TOKEN' : localStorage.getItem('token')}
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

export default LoginService;
