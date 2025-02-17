package com.tiki.server.timeblock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tiki.server.team.entity.Team;
import com.tiki.server.timeblock.entity.TimeBlock;

public interface TimeBlockRepository extends JpaRepository<TimeBlock, Long> {

	void deleteAllByTeamId(long teamId);

	@Query(value = "select * from time_block "
		+ "where team_id = :teamId and accessible_position = :position and "
		+ "(to_char(start_date, 'YYYY-MM') <= :date and to_char(end_date, 'YYYY-MM') >= :date) "
		+ "order by start_date asc", nativeQuery = true)
	List<TimeBlock> findByTeamAndAccessiblePositionAndDate(long teamId, String position, String date);
}
