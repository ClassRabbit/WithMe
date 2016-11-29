var express = require('express');
var User = require('../models/User');
var Room = require('../models/Room');
var Join = require('../models/Join');
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
      newRoom.address = body.address;
      newRoom.limit = body.limit;
      newRoom.numberOfImages = files.image.length;

      newRoom.save(function(err, room){
        if(err) {
          console.log("방 등록 실패");
          return res.json({result: 'fail'});
        }
        console.log("방 등록 성공");
        var newJoin = new Join();
        newJoin.user = body.user;
        newJoin.room = room.id;
        newJoin.position = 'owner';

        newJoin.save(function(err, join){
          if(err) {
            console.log("조인 등록 실패");
            return res.json({result: 'fail'});
          }
          console.log("조인 등록 성공");
          var dirPath = "./public/images/room/" + room.id;
          ensureExists(dirPath, 0777, function (err){
            if(err){
              console.log("mkdir ERR!!");
              throw err;
            }
            else{
              console.log('Created newdir');
            }
          });
          console.log("Length : " + files.image.length);
          for (var j=0; j<files.image.length; j++){
            // console.log(files.image[j]);
            imageUpload(files.image[j], dirPath);
          }
          return res.json({result: 'success'});
        });
      });
     //put in here all the logic applied to your files.
  });
});

function imageUpload(file, dirPath){
  fs.readFile(file.path, function(err, data){
      var filePath = dirPath + "/" + file.originalFilename;
      fs.writeFile(filePath, data, function(err){
        if(err){
          console.log(err);
        }
      });
  });
}

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

router.post('/view', function (req, res){
  console.log(req.body);

  Join.findOne({user: req.body.user}, function(err, join){
    if(err){
      return res.json({result: 'error'});
    }
    Room.findById(req.body.roomId, function(err, room){
      if(err){
        return res.json({result: 'error'});
      }
      if(room === null){
        return res.json({result: 'null'});
      }
      User.findById(room.user, function(err, user){
        if(err){
          return res.json({result: 'error'});
        }
        if(user === null) {
          //user가 없을때 지만 이런일이 안생기게 막음
          return;
        }
        Join.find({room: room.id}, function(err, joins){
          if(err){
            return res.json({result: 'error'});
          }
          if(joins === null) {
            //joins가 없을때 지만 이런일이 안생기게 막음
            return;
          }
          var query = [];
          for(var i in joins){
            query.push(joins[i].user);
          }
          console.log('joins count : ' + joins.length);
          User.find({_id:{ $in: query }}, function(err, users){
            if(err){
              return res.json({result: 'error'});
            }
            var data = [];
            data.push(room);
            data.push(user);
            data.push(joins);
            data.push(users);
            if(join === null){
              return res.json({isJoin: false, data: data});
            }
            else {
              return res.json({isJoin: true, data: data});
            }
          });
          // return res.json({data: data});
          // if(join === null){
          //   return res.json({isJoin: false, data: data});
          // }
          // else {
          //   return res.json({isJoin: true, data: data});
          // }
        });
      });
    });
  });
});

router.post('/join', function(req, res, next) {
  var newJoin = new Join();
  newJoin.user = req.body.user;
  newJoin.room = req.body.room;
  newJoin.position = 'waiting';

  newJoin.save(function(err, join){
    if(err) {
      console.log("조인 등록 실패");
      return res.json({result: 'fail'});
    }
    console.log("조인 등록 성공");
    return res.json({result: 'success'});
  });
});

router.post('/ack', function(req, res, next) {
  Join.findById(req.body.joinId, function(err, join){
    if(err) {
      console.log("조인 수정 실패");
      return res.json({result: 'fail'});
    }
    if(join === null){
      console.log("조인 없음");
      return res.json({result: 'fail'});
    }
    join.position = 'constitutor';
    join.save(function(err, join){
      if(err) {
        console.log("조인 수정 실패");
        return res.json({result: 'fail'});
      }
      console.log("조인 수정 성공");
      return res.json({result: 'success'});
    });
  });
});

router.post('/refuce', function(req, res, next) {
  console.log("삭제 : " + req.body.joinId);
  Join.findOneAndRemove({_id:req.body.joinId}, function(err){
    if(err) {
      console.log("조인 거절 실패");
      return res.json({result: 'fail'});
    }
    console.log("조인 거절 성공");
    return res.json({result: 'success'});
  });
});

router.post('/secession', function(req, res, next) {
  console.log("삭제 : " + req.body.user);
  Join.findOneAndRemove({user:req.body.user}, function(err){
    if(err) {
      console.log("조인 삭제 실패");
      return res.json({result: 'fail'});
    }
    console.log("조인 삭제 성공");
    return res.json({result: 'success'});
  });
});

router.post('/joinCancle', function(req, res, next) {
  console.log("삭제 : " + req.body.user);
  Join.findOneAndRemove({user:req.body.user}, function(err){
    if(err) {
      console.log("조인 삭제 실패");
      return res.json({result: 'fail'});
    }
    console.log("조인 삭제 성공");
    return res.json({result: 'success'});
  });
});


module.exports = router;
