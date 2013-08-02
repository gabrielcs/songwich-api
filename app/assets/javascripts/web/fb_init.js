/*
 FB.init({ 
    appId: '291135277698838',          //ID da aplicação web definido no Facebook
	cookie: true,                               // permitir cookies para poder acessar a sessão
	status: true,                               // verificar o status do login
	xfbml: false,                              // usar ou não tags do Facebook
	oauth: true                                // autenticação via OAuth 2.0
 });*/
window.fbAsyncInit = function() {
	FB.init({
		appId : '291135277698838',
		status : true,
		cookie : true,
		xfbml : true
	});
	// Additional initialization code such as adding Event Listeners goes here

	FB.Event.subscribe('auth.statusChange', function(response) {
		if (response.status === 'connected') {
			// the user is logged in and has authenticated your app, and
			// response.authResponse supplies
			// the user's ID, a valid access token, a signed request, and the
			// time the access token
			// and signed request each expire
			// alert("connected!!");

			var uid = response.authResponse.userID;
			var accessToken = response.authResponse.accessToken;
			// login(accessToken);
			aboutMe(response);
			$('#logout-button').show();
			$('#LoggedOutDiv').show();
		} else if (response.status === 'not_authorized') {
			// the user is logged in to Facebook,
			// but has not authenticated your app
			// alert("not authorized!!")
			$('#logout-button').hide();
			$('#LoggedOutDiv').show();
		} else {
			// the user isn't logged in to Facebook.
			// alert("not logged in yet!!")
			$('#picture').attr('src', '');
			$('#logout-button').hide();
			$('#LoggedOutDiv').show();
			$('#LoggedInDiv').hide();
		}
	});
};
// Load the SDK asynchronously
(function(d, s, id) {
	var js, fjs = d.getElementsByTagName(s)[0];
	if (d.getElementById(id)) {
		return;
	}
	js = d.createElement(s);
	js.id = id;
	js.src = "//connect.facebook.net/en_US/all.js";
	fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));

function aboutMe() {
	FB.api('/me/picture', function(response) {
		console.log('picture : ' + response.data.url + '?type=square');
		$('#picture').attr('src', response.data.url + '?type=square');
		$('#logout-button').show();
		$('#LoggedOutDiv').hide();
		$('#LoggedInDiv').show();
	});
	FB.api('/me', function(response) {
		console.log('Good to see you, \n' + '\n id : ' + response.id
				+ '\n locale : ' + response.locale + '\n name : '
				+ response.name + '\n first_name : ' + response.first_name
				+ '\n middle_name : ' + response.middle_name
				+ '\n last_name : ' + response.last_name + '\n gender : '
				+ response.gender + '\n link : ' + response.link
				+ '\n username : ' + response.username + '\n email : '
				+ response.email);
				document.getElementById("name").value = response.name;
				document.getElementById("devEmail").value = response.email;
		
		// ARRAYS:/
		/*
		 * + '\n likes : ' + response.likes + '\n music : ' + response.music
		 * //requires: user_likes or friends_likes. + '\n user_actions.music :' +
		 * response.music //requires: friends_actions.music +'.');
		 * 
		 * friendCount = response.data.length; for( i=0; i<response.data.length;
		 * i++) { friendId = response.data[i].id; FB.api('/'+friendId+'/movies',
		 * function(response) { movieList = movieList.concat(response.data);
		 * friendCount--; document.getElementById('test').innerHTML =
		 * friendCount + " friends to go ... "; if(friendCount === 0) {
		 * data_fetch_postproc(); }; }); }
		 */
	});
}

function loginWithFacebook() {
	FB.login(function(response) {
		if (response.authResponse) {
			window.location = "http://localhost:9000/dev"; // #fb_token="+response.authResponse.accessToken;
		} else {
			console.log('User cancelled login or did not fully authorize.');
		}
	}, {
		scope : 'email,user_likes,friends_likes,friends_actions.music'
	});
};

function logout() {
	FB.logout(function(response) {
		// alert("Person is now logged out");
		$('#picture').attr('src', '');
		$('#logout-button').hide();
		$('#LoggedOutDiv').show();
		$('#LoggedInDiv').hide();
	});
};

function feedback(msg){
	bootstrap_alert.warning(msg);
};
bootstrap_alert = function() {
}
bootstrap_alert.warning = function(
		message) {
	$('#alert_placeholder')
			.html(
					'<div class="alert"><a class="close" data-dismiss="alert">x</a><span>'
							+ message
							+ '</span></div>')
}

$(document).ready(function() {
	
	$('#submitButton').bind('click', function() {
	    $.post('http://localhost:9000/postAppDeveloper', 
	       $('#developerForm').serialize(), 
	       function(data, status, xhr){
	         // do something here with response;
	    	feedback(data + " " + status);
	    	
	       });
	    
	});
	
	$('#logout-button').bind('click', function() {
		logout();
	});


	$('#LoggedOutDiv').show();
	$('#LoggedInDiv').hide();
	/*
	 * //pull the access token out of the query string var ql = []; if
	 * (window.location.hash) { // split up the query string and store in an
	 * associative array var params = window.location.hash.slice(1).split("#");
	 * var tmp = params[0].split("&"); alert(tmp.length); for (var i = 0; i <
	 * tmp.length; i++) { var vals = tmp[i].split("="); ql[vals[0]] =
	 * unescape(vals[1]); } }
	 * 
	 * if (ql['fb_token']) { var facebookAccessToken = ql['fb_token']; //try to
	 * log in with facebook alert(facebookAccessToken); }
	 */
});