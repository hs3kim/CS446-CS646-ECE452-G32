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

exports.editFarm = async (req, res) => {
  const { name, code } = req.body;
  const user = req.user || {};

  if (!user || !user.userID || !name) {
    throw new AppError("BAD_REQUEST");
  }
  updatedFarm = await farmService.editFarm(user.userID, code, name)

  res.status(201).json(formatSuccessResponse(updatedFarm));
};

exports.deleteFarm = async (req, res) => {
  const deletedFarm = await farmService.deleteFarm(user.userID, farmCode);

  res.status(201).json(formatSuccessResponse(deletedFarm));
};

exports.enrollWorker = async (req, res) => {
  const { farmCode } = req.body;
  const user = req.user || {};

  if (!user || !user.userID || !farmCode) {
    throw new AppError("BAD_REQUEST");
  }
  
  const farm = await farmService.getFarmByCode(farmCode);
  if (!farm) {
    throw new AppError("NOT_FOUND");
  }

  await userService.addWorkedFarm(farm._id, user.userID);
  await farmService.addWorker(farm._id, user.userID);

  res.status(200).json(formatSuccessResponse());
};
