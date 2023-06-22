const jwt = require("jsonwebtoken");

const { User } = require("../models/user");
const { AppError } = require("../utils/errors");

exports.checkDuplicateUser = async (username) => {
  const user = await User.findOne({ username });
  if (user) {
    console.log({ user });
    throw new AppError("DUPLICATE_RECORD", "Username Already in Use");
  }
};

exports.createUser = async (username, email, hashedPassword) => {
  const user = await User.create({
    username,
    email: email.toLowerCase(),
    password: hashedPassword,
  });
  return user;
};

exports.createJWTSignature = (user) => {
  const token = jwt.sign(
    {
      userID: user._id,
      username: user.username,
      email: user.email,
    },
    process.env.JWT_TOKEN_KEY
  );
  return token;
};
