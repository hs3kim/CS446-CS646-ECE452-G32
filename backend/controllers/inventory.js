const { AppError } = require("../utils/errors");
const inventoryService = require("../services/inventoryService");
const farmService = require("../services/farmService");
const { getQueryDict } = require("../utils/parseText");
const { formatSuccessResponse } = require("../utils/formatResponse");

exports.addInventory = async (req, res) => {
  const { texts, farmCode } = req.body;
  if (!farmCode || !texts || !Array.isArray(texts)) {
    throw new AppError("BAD_REQUEST");
  }

  for (const text of texts) {
    const query = getQueryDict(text);
    await inventoryService.addCropInventory(farmCode, query);
  }

  res.status(201).json(formatSuccessResponse(null));
};

exports.getInventory = async (req, res) => {
  const { farmCode } = req.query;
  if (!farmCode) {
    throw new AppError("BAD_REQUEST");
  }

  const farm = await farmService.getFarmByCode(farmCode);
  if (!farm) {
    throw new AppError("NOT_FOUND");
  }

  const inventory = await inventoryService.getInventory(farm._id);

  res.status(200).json(formatSuccessResponse(inventory));
};
