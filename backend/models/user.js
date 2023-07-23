const mongoose = require("mongoose");

const userSchema = new mongoose.Schema({
  username: {
    type: String,
    unique: true,
  },
  email: String,
  password: String,
  owns: [String],
  worksAt: [String],
});

const User = mongoose.model("User", userSchema);

module.exports = { User };
