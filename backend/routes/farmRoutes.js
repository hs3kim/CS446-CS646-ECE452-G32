const express = require("express");

const { createFarm, editFarm, deleteFarm } = require("../controllers/farm");

const router = express.Router();

router.post("/new", createFarm);
router.post("/remove", deleteFarm);
router.post("/edit", editFarm);

module.exports = router;
