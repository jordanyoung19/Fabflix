use moviedb;

-- add_movie stored procedure
DELIMITER $$

create procedure add_movie(in inTitle varchar(100), in inYear int, in inDirector varchar(100), in inStar varchar(100), in inGenre varchar(100), out res varchar(100))
begin
	-- set declared variables to be used in procedure 
	declare declaredMovieId varchar(10);
	declare declaredStarId varchar(10);
	declare declaredGenreId int;

	-- check if movie already exists
	select id into declaredMovieId
	from movies 
	where title = inTitle and year = inYear and director = inDirector 
	limit 1;
	if (declaredMovieId is null) then
		-- movie DNE, so create new movie and add to movies table
		call get_new_id('movies', declaredMovieId);
		insert into movies(id, title, year, director) values (declaredMovieId, inTitle, inYear, inDirector);
		
		-- see if genre already exists
		select g.id into declaredGenreId 
		from genres as g
		where g.name = inGenre limit 1;
		
		if (declaredGenreId is null) then
			-- if not, add genre to genres table
			insert into genres (name) values (inGenre);
            -- get new genre id and put into declaredGenreId
			select g.id into declaredGenreId
            from genres as g
			where g.name = inGenre limit 1;
		end if;
		-- link genre and movie in genres_in_movies table
		insert into genres_in_movies (genreId, movieId) values (declaredGenreId, declaredMovieId);
		
		-- see if star already exists
		select s.id into declaredStarId
		from stars as s
		where s.name = inStar limit 1;
		
		if (declaredStarId is null) then
			-- if not, add star to stars table but must get new ID first
			call get_new_id('stars', declaredStarId);
			insert into stars (id, name) values (declaredStarId, inStar);
		end if;
		-- link star to movie in stars_in_movies table
		insert into stars_in_movies (starId, movieId) values (declaredStarId, declaredMovieId);

        insert into ratings (movieId, rating, numVotes) values (declaredMovieId, 0.0, 0);
		
		set res = concat('new movie id: ', declaredMovieId, ' new genre id: ', declaredGenreId, ' new star id: ', declaredStarId);
	else 
		set res = 'Error: movie already exists';
	end if;
	select res;
end; 
$$

delimiter ;
