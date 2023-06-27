const express = require("express");

const { createFarm } = require("../controllers/farm");

const router = express.Router();

router.post("/new", createFarm);

module.exports = router;
