const { AppError } = require("../utils/errors");
const authService = require("../services/authService");
const { formatSuccessResponse } = require("../utils/formatResponse");

exports.registerUser = async (req, res) => {
  const { username, email, password } = req.body;

  if (!username || !email || !password) {
    throw new AppError("BAD_REQUEST");
  }

  await authService.checkDuplicateUser(username);

  const createdUser = await authService.createUser(username, email, password);

  const jwtToken = authService.createJWTSignature(createdUser);

  res
    .status(201)
    .setHeader("Set-Cookie", `FarmWiseKey=${jwtToken}`)
    .json(formatSuccessResponse(createdUser));
};

exports.loginUser = async (req, res) => {
  const { username, password } = req.body;

  if (!username || !password) {
    throw new AppError("BAD_REQUEST");
  }

  const user = await authService.loginUser(username, password);

  const jwtToken = authService.createJWTSignature(user);

  res
    .status(200)
    .setHeader("Set-Cookie", `FarmWiseKey=${jwtToken}`)
    .json(formatSuccessResponse(user));
};
