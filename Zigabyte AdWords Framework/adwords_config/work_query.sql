CREATE TEMPORARY TABLE T1 AS SELECT 
    b.address as ADDRESS, 
    count( * ) AS N, 
    min( `LP_LIST_PRICE` ) as MIN_ALL, 
    max( `LP_LIST_PRICE` ) as MAX_ALL, 
    min(IF(BR_BEDROOMS=1,LP_LIST_PRICE,NULL)) as MIN_1BR, 
    min(IF(BR_BEDROOMS=2,LP_LIST_PRICE,NULL)) as MIN_2BR, 
    IF(f_geo_valid(i.CTCX_LATITUDE, i.CTCX_LONGITUDE), 
       ctcx_neighborhood, AR_AREA_NAME) as AREA
FROM `idx_search_keys` i, geo_buildings b, const_areas 
WHERE i.building_id = b.building_id 
    AND `TYP_PROPERTY_TYPE` = 'AT' 
    AND ST_STATUS NOT IN ('SOLD','CTG')
    AND areaCode = `AR_AREA` AND indexable = 'Y' 
GROUP BY address
