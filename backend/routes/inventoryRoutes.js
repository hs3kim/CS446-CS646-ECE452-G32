const express = require("express");

const { addInventory, getInventory } = require("../controllers/inventory");

const router = express.Router();

router.get("/get", getInventory);
router.post("/update", addInventory);

module.exports = router;
