DROP INDEX IF EXISTS idx_order_status;
DROP INDEX IF EXISTS idx_role;
DROP INDEX IF EXISTS idx_order_id;
DROP INDEX IF EXISTS idx_login;
DROP INDEX IF EXISTS idx_itemName;
DROP INDEX IF EXISTS idx_itemType;
DROP INDEX IF EXISTS idx_price;


CREATE INDEX idx_order_status ON FoodOrder(orderStatus);
CREATE INDEX idx_role ON Users(role);
CREATE INDEX idx_order_id ON FoodOrder(orderID);

CREATE INDEX idx_login ON Users USING HASH (login);
CREATE INDEX idx_itemName ON Items USING HASH (itemName);
CREATE INDEX idx_itemType ON Items USING HASH (typeOfItem);
CREATE INDEX idx_price ON Items(Price);

