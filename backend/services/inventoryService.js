const { CropInventory, Farm } = require("../models/inventory");
const { AppError } = require("../utils/errors");

exports.addCropInventory = async (farmCode, productInfo) => {
    const farm = await Farm.findOne({ code: farmCode});
    const product = productInfo.item;
    const count = product.quantity;
    if (!farm) {
        throw new AppError("BAD_REQUEST", "Farm Does Not Exist");
    }
    invenEntry = await CropInventory.findOne({farm: farm._id, product: product});
    if (invenEntry) {
        invenEntry.count += count;
        try {
            invenEntry.save()
        } catch(error) {
            throw new AppError("THIRD_PARTY_ERROR", "Inventory has not been updated");
        };
        return invenEntry
    } else {
        const invenEntry = await ropInventory.create({
            farm: farm.id, 
            product: product, 
            count: count
        });
        return invenEntry;
    }
};
