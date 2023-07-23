const express = require("express");

const { updateInventory, getInventory } = require("../controllers/inventory");

const router = express.Router();

router.get("/get", getInventory);
router.post("/update", updateInventory);

module.exports = router;
