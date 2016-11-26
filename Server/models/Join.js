var mongoose = require('mongoose'),
    Schema = mongoose.Schema;

var schema = new Schema({
  user: {type: Schema.Types.ObjectId, required: true, unique: true},
  room: {type: Schema.Types.ObjectId, required: true},
  position: {type: String, required: true},
  joinedAt: {type: Date, default: Date.now}
}, {
  toJSON: {virtuals: true},
  toObject: {virtuals: true}
});

var Join = mongoose.model('Join', schema);

module.exports = Join;
