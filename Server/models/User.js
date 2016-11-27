var mongoose = require('mongoose'),
    bcrypt = require('bcryptjs'),
    Schema = mongoose.Schema;

var schema = new Schema({
  mail: {type: String, unique: true, required: true},
  password: {type: String, required: true},
  token: {type: String},
  name: {type: String, required: true},
  birth: {type: String, required: true},
  phone: {type: String, required: true},
  gender: {type: String, required: true},
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
