const { AppError } = require("../utils/errors");
const farmService = require("../services/farmService");
const { formatSuccessResponse } = require("../utils/formatResponse");

exports.createFarm = async (req, res) => {
  const { name } = req.body;
  const user = req.user || {};

  if (!user || !user.userID || !name) {
    throw new AppError("BAD_REQUEST");
  }

  const newFarm = await farmService.createFarm(user.userID, name);

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
  const { farmCode } = req.body;
  const user = req.user || {};

  if (!user || !user.userID || !farmCode) {
    throw new AppError("BAD_REQUEST");
  }

  const deletedFarm = await farmService.deleteFarm(user.userID, farmCode);

  res.status(201).json(formatSuccessResponse(deletedFarm));
};