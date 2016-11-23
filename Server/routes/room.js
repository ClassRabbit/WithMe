var express = require('express');
var User = require('../models/User');
var Room = require('../models/Room');
var router = express.Router();
var multipart = require('multiparty');
var fs = require('fs');
/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Room' });
});

router.post('/create', function(req, res, next) {
  var form = new multipart.Form();
    form.parse(req, function(err, fields, files) {
      var newRoom = new Room();
      var body = JSON.parse(fields.body);
      // 임시 유저 아이디.
      console.log("*******Body*******");
      console.log(body);
      console.log("******/Body*******");
      // console.log("files.image[2]******************");
      // console.log(files.image[2]);
      // console.log("****files++**********");
      newRoom.user = body.user;
      newRoom.title = body.title;
      newRoom.content = body.content;
      newRoom.latitude = body.latitude;
      newRoom.longitude = body.longitude;
      newRoom.limit = body.limit;

      var dirPath = "./public/images/room/" + body.user;
      ensureExists(dirPath, 0777, function (err){
        if(err){
          console.log("mkdir ERR!!");
          throw err;
        }
        else{
          console.log('Created newdir');
        }
      });
      function imageUpload(file){
        fs.readFile(file.path, function(err, data){
            var filePath = dirPath + "/" + file.originalFilename;
            fs.writeFile(filePath, data, function(err){
              if(err){
                console.log(err);
              }
            });
        });
      }
      console.log("Length : "+files.image.length);
      for (var j=0; j<files.image.length; j++){
        // console.log(files.image[j]);
        imageUpload(files.image[j]);
      }
      newRoom.save(function(err, user){
        if(err) {
          console.log("사용자 등록 실패");
          return res.json({result: 'fail'});
        }
        console.log("사용자 등록 성공");
        return res.json({result: 'success'});
      });
     //put in here all the logic applied to your files.
  });
});


function ensureExists(path, mask, cb) {
    if (typeof mask == 'function') { // allow the `mask` parameter to be optional
        cb = mask;
        mask = 0777;
    }
    fs.mkdir(path, mask, function(err) {
        if (err) {
            if (err.code == 'EEXIST') cb(null); // ignore the error if the folder already exists
            else cb(err); // something else went wrong
        } else cb(null); // successfully created folder
    });
}
module.exports = router;
