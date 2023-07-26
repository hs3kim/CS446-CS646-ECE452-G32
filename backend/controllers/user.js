const { AppError } = require("../utils/errors");
const userService = require("../services/userService");
const farmService = require("../services/farmService");
const { formatSuccessResponse } = require("../utils/formatResponse");

exports.getUserDetails = async (req, res) => {
  if (!req.user || !req.user.userID) {
    throw new AppError("BAD_REQUEST");
  }
  let user = await userService.getUserByID(req.user.userID);
  if (!user) {
    throw new AppError("NOT_FOUND");
  }
  user = user.toObject();
  user.password = "";

  user.owns = await farmService.getFarmByIDs(user.owns);
  user.worksAt = await farmService.getFarmByIDs(user.worksAt);

  res.status(200).json(formatSuccessResponse(user));
};

exports.editUser = async (req, res) => {
  const user = req.user || {};

  if (!user || !user.userID || !req.body) {
    throw new AppError("BAD_REQUEST");
  }
  updatedUser = await userService.updateUserProfile(user.userID, req.body)
  updatedUser.password = ''

  res.status(201).json(formatSuccessResponse(updatedUser));
};