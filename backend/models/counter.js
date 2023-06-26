const mongoose = require("mongoose");

/*

Seems the mongoose-auto-increment package is no longer compatible 
with the latest version of mongoose.

Hence, we will use a custom Counter to keep track of different values
we need to increment.

*/

const counterSchema = new mongoose.Schema({
  name: { type: String, unique: true },
  seq: { type: Number, default: 0 },
});

counterSchema.index({ name: 1, seq: 1 }, { unique: true });

const Counter = mongoose.model("Counter", counterSchema);

module.exports = { Counter };
