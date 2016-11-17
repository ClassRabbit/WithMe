var mongoose = require('mongoose'),
    bcrypt = require('bcryptjs'),
    Schema = mongoose.Schema;

var schema = new Schema({
  mail: {type: String, required: true, unique: true},
  password: {type: String, required: true, unique: true},
  token: {type: String, unique: true},
  name: {type: String, unique: true},
  birth: {type: String, unique: true},
  phone: {type: Number, unique: true},
  gender: {type: String, unique: true},
  img: {type: String}
}, {
  toJSON: {virtuals: true},
  toObject: {virtuals: true}
});

schema.methods.generateHash = function(password) {
  var salt = bcrypt.genSaltSync(10);
  return bcrypt.hashSync(password, salt);
};

schema.methods.validatePassword = function(password) {
  return bcrypt.compareSync(password, this.password);
};

var Product = mongoose.model('User', schema);

module.exports = Product;
