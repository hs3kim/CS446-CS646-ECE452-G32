const jwt = require("jsonwebtoken");
const bcrypt = require("bcryptjs");

const { User } = require("../models/user");
const { AppError } = require("../utils/errors");

exports.checkDuplicateUser = async (username) => {
  const user = await User.findOne({ username });
  if (user) {
    throw new AppError("DUPLICATE_RECORD", "Username Already in Use");
  }
};

exports.createUser = async (username, email, password) => {
  const hashedPassword = await bcrypt.hash(password, 10);

  const user = await User.create({
    username,
    email: email.toLowerCase(),
    password: hashedPassword,
  });

  user.password = "";
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

exports.loginUser = async (username, password) => {
  const user = await User.findOne({ username });
  if (!user) {
    throw new AppError("BAD_REQUEST", "User Does Not Exist");
  }

  const passwordMatch = await bcrypt.compare(password, user.password);
  if (!passwordMatch) {
    throw new AppError("BAD_REQUEST", "Invalid Username or Password");
  }

  user.password = "";
  return user;
};

exports.changePassword = async (username, currentPassword, newPassword) => {
  const user = await User.findOne({ username });
  if (!user) {
    throw new AppError("BAD_REQUEST", "User Does Not Exist");
  }

  const isPasswordValid = await bcrypt.compare(currentPassword, user.password);

  if (!isPasswordValid) {
    throw new AppError("BAD_REQUEST", "Incorrect current password");
  }

  // Hash the new password and update it in the database
  const hashedNewPassword = await bcrypt.hash(newPassword, 10);
  user.password = hashedNewPassword;
  await user.save();
};