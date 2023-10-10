
-- 1. 52 Weeks of Sales History: select count of orders grouped by week. 
SELECT EXTRACT (WEEK FROM order_date) AS week_number, COUNT(*) AS order_count 
FROM order_history
GROUP BY EXTRACT (WEEK FROM order_date)
ORDER BY week_number;


-- 2. Realistic Sales History: select count of orders, sum of order total grouped by hour.
SELECT
    EXTRACT(HOUR FROM order_time) AS hour_of_day,
    COUNT(order_id) AS order_count,
    SUM(total_cost) AS total_order_cost
FROM order_history
GROUP BY EXTRACT(HOUR FROM order_time)
ORDER BY EXTRACT(HOUR FROM order_time);
