NUMBER=`cat counter.txt` # load counter
NEWNUMBER=`expr $NUMBER + 1`	#increment counter to save it to file
sed -i "s/$NUMBER/$NEWNUMBER/g" counter.txt # save new counter to file
POLYLINES='/home/marcin/Downloads/polylines.csv'
cat $POLYLINES > tmp.txt
rm $POLYLINES
sed -i '1,2d' tmp.txt
sed -i '$ d' tmp.txt
sed -i 's/\r/),/' tmp.txt # add ), in the last of every line in data
sed -i "1s/^/(select latitude from places where name = '$1'), (select longitude from places where name = '$1')),\n/" tmp.txt # add first point
echo "(select latitude from places where name = '$2'), (select longitude from places where name = '$2')), " >> tmp.txt # add last point
sed -i "s/^/($NUMBER, /" tmp.txt	# add trail_id in every line
cat tmp.txt >> 4_insert_into_trails_points.sql

case $3 in
blue) COLOR='-16776961' ;;
y) COLOR='-256' ;;
g) COLOR='-16711936' ;;
black) COLOR='-16777216' ;;
r) COLOR='-65536' ;;
esac

echo "($NUMBER, $COLOR, (select id from places where name = '$1'), (select id from places where name = '$2'), $4, $5)," >> 3_insert_into_trails.sql
