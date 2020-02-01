select 
    AREA, SUM(N) as N_AREA, 
    min(MIN_ALL) as MIN_ALL, 
    max(MAX_ALL) as MAX_ALL, 
    min(MIN_1BR) as MIN_1BR, 
    min(MIN_2BR) as MIN_2BR 
from T1 
where N<2 
group by AREA
having N_AREA > 2 and MIN_1BR is not null AND MIN_2BR is not null