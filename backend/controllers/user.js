const { AppError } = require("../utils/errors");
const userService = require("../services/userService");
const { formatSuccessResponse } = require("../utils/formatResponse");

exports.getUserDetails = async (req, res) => {
  const { userID } = req.query;
  if (!userID) {
    throw new AppError("BAD_REQUEST");
  }
  const user = await userService.getUserByID(userID);
  if (!user) {
    throw new AppError("NOT_FOUND");
  }
  user.password = "";
  res.status(200).json(formatSuccessResponse(user));
};
