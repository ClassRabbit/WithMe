var express = require('express');
var router = express.Router();
var FCM = require('fcm-push');

var serverkey = 'AIzaSyBoQ8zUeNyIZ0yCMDx-5BVh55W-HwpbRO4';
var fcm = new FCM(serverkey);

var message = {
    to: 'registration_token_or_topics', // required fill with device token or topics
    collapse_key: 'your_collapse_key',
    data: {
        data1: 'Data1 입니다.',
        data2: 'Data2 입니다.'
    },
    notification: {
        // title: 'Title of your push notification',
        // body: 'Body of your push notification'
    }
};

router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.post('/', function (req, res){
	// console.log(req.body.token);
  message.to = req.body.token;
  console.log(message.to);
  fcm.send(message, function(err,response){
      if(err) {
          console.log("Something has gone wrong !");
      } else {
          console.log("Successfully sent with resposne :",response);
      }
  });
  res.json({userId:'testId', password:'testPassWord'});
});

module.exports = router;
