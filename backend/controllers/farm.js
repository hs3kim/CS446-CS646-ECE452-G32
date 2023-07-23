const { AppError } = require("../utils/errors");
const farmService = require("../services/farmService");
const userService = require("../services/userService");
const { formatSuccessResponse } = require("../utils/formatResponse");

exports.createFarm = async (req, res) => {
  const { name } = req.body;
  const user = req.user || {};

  if (!user || !user.userID || !name) {
    throw new AppError("BAD_REQUEST");
  }

  const newFarm = await farmService.createFarm(user.userID, name);
  await userService.addOwnedFarm(newFarm._id, user.userID);

  res.status(201).json(formatSuccessResponse(newFarm));
};
