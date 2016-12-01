SELECT E.seg_id, E.node1, E.node2, N1.lat AS lat1, N1.lon AS lon1, N2.lat AS lat2, N2.lon AS lon2 FROM tmp_edges AS E INNER JOIN tmp_nodes AS N1 ON E.node1 = N1.node_id INNER JOIN tmp_nodes AS N2 ON E.node2 = N2.node_id
 
	
