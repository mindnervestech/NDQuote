import config from '../config'; 
import fetch from 'node-fetch';
const TokenService = {
  getTokenListData(userId) {
  	return new Promise((resolve, reject) =>{
  		fetch( config.baseURL + 'ndquote/api/tokens/'+userId, {
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
	checkTokenFlag(tokenId,tokenString) {
		return new Promise((resolve, reject) =>{
			fetch( config.baseURL + 'ndquote/api/tokens/check/'+ tokenId +'/'+tokenString , {
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
	getSingleToken(userId,tokenId) {
		return new Promise((resolve, reject) =>{
			fetch( config.baseURL + 'ndquote/api/tokens/'+ userId +'/'+tokenId , {
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
	}

};

export default TokenService;
