const bcrypt = require("bcryptjs");

const { AppError } = require("../utils/errors");
const authService = require("../services/authService");
const { formatSuccessResponse } = require("../utils/formatResponse");

exports.registerUser = async (req, res) => {
  const { username, email, password } = req.body;

  if (!username || !email || !password) {
    throw new AppError("BAD_REQUEST");
  }

  await authService.checkDuplicateUser(username);

  const hashedPassword = await bcrypt.hash(password, 10);

  const createdUser = await authService.createUser(
    username,
    email,
    hashedPassword
  );

  createdUser.password = "";

  const jwtToken = authService.createJWTSignature(createdUser);

  res
    .status(201)
    .setHeader("Set-Cookie", `FarmWiseKey=${jwtToken}`)
    .json(formatSuccessResponse(createdUser));
};
