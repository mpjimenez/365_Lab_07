select lab7_rooms.*, sum(daysOccupied)/180 PopularityScore
from lab7_rooms
    inner join (
        select room,
        case
            when checkin > date_sub(curdate(), interval 180 day) then datediff(checkout, checkin)
            else datediff(checkout, date_sub(curdate(), interval 180 day))
        end daysOccupied
        from lab7_reservations
        where checkout > date_sub(curdate(), interval 180 day)
    ) a on room = roomCode
group by roomCode
order by PopularityScore desc;

select room, date_sub(curdate(), interval 180 day) 180daysAgo,
case
    when checkin > date_sub(curdate(), interval 180 day) then datediff(checkout, checkin)
    else datediff(checkout, date_sub(curdate(), interval 180 day))
end daysOccupied
from lab7_reservations
where checkout > date_sub(curdate(), interval 180 day);

select lab7_rooms.* from lab7_rooms  
	inner join lab7_reservations on Room  = RoomCode 
	where bedType = 'Queen' and CheckIn = '2017-10-23' and Checkout = '2017-10-25' and Kids = 0 and Adults = 1;
