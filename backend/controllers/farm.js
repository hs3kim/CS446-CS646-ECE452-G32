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
  const { name, farmCode } = req.body;
  const user = req.user || {};

  if (!user || !user.userID || !name) {
    throw new AppError("BAD_REQUEST");
  }
  updatedFarm = await farmService.editFarm(user.userID, farmCode, name);

  res.status(201).json(formatSuccessResponse(updatedFarm));
};

exports.deleteFarm = async (req, res) => {
  const { farmCode } = req.body;
  const user = req.user || {};

  if (!user || !user.userID || !farmCode) {
    throw new AppError("BAD_REQUEST");
  }
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

  if (farm.owner == user.userID) {
    throw new AppError("BAD_REQUEST", "You are the owner of this farm");
  }

  if (farm.employees.includes(user.userID)) {
    throw new AppError("BAD_REQUEST", "You are already enrolled in this farm");
  }

  await userService.addWorkedFarm(farm._id, user.userID);
  await farmService.addWorker(farm._id, user.userID);

  res.status(200).json(formatSuccessResponse());
};

exports.unenrollWorker = async (req, res) => {
  const { farmCode } = req.body;
  const user = req.user || {};

  if (!user || !user.userID || !farmCode) {
    throw new AppError("BAD_REQUEST");
  }

  const farm = await farmService.getFarmByCode(farmCode);
  if (!farm) {
    throw new AppError("NOT_FOUND");
  }

  if (farm.owner == user.userID) {
    throw new AppError("BAD_REQUEST", "You are the owner of this farm");
  }

  if (!farm.employees.includes(user.userID)) {
    throw new AppError("BAD_REQUEST", "You are not enrolled in this farm");
  }

  await userService.removeWorkedFarm(farm._id, user.userID);
  await farmService.removeWorker(farm._id, user.userID);

  res.status(200).json(formatSuccessResponse());
};
