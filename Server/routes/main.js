var express = require('express');
var Join = require('../models/Join');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.post('/', function (req, res){
  console.log(req.body);
  Join.findOne({user: req.body.user, password: req.body.password}, function(err, join){
    if(err){
      return res.json({result: 'error'});
    }
    if(join == null){
      return res.json({isJoin: false});
    }
    else {
      return res.json({isJoin: true});
    }
  });
});

module.exports = router;
