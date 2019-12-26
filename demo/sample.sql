SELECT A.SER_NUM, B.MAX_FLAG, A.DEAL_CO_CD, A.DEL_YN, A.UNIT
FROM   MGMT A,
  (SELECT A.SER_NUM, A.DEAL_CO_CD, 'X' AS MAX_FLAG
   FROM MGMT A
   WHERE (A.DEAL_CO_CD ,A.SER_NUM) IN (SELECT  B.DEAL_CO_CD ,                                             
                                       MAX(B.SER_NUM) AS SER_NUM
                                       FROM MGMT B
                                       WHERE   B.DEL_YN = 'N'
                                       GROUP BY B.DEAL_CO_CD)
  ) B
WHERE A.SER_NUM = B.SER_NUM(+) 
AND A.DEAL_CO_CD = B.DEAL_CO_CD (+)
AND A.DEL_YN = 'N'
ORDER BY A.DEAL_CO_CD, A.SER_NUM;
