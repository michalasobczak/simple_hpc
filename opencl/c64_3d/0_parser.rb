#puts "Reading file `ARGV[0]`..."
# OPEN FILE
file = File.new(ARGV[0], "r")
# MAIN LOOP
lines = []
while (line = file.gets)
  lines << line
end # while
# CLOSE FILE
file.close
# VERTICES
#puts "===> VERTICES"
vertices_count = 0
lines.each_with_index do |line,index|
  #puts index
  split_line = line.split(" ")
  first_element = split_line[0]
  if first_element == 'v' then
    x = (split_line[1].to_f*100).to_i
    y = (split_line[2].to_f*100).to_i
    z = (split_line[3].to_f*100).to_i
    print "#{x},#{y},#{z},\n"
    vertices_count=vertices_count+1
  end # if
end # lines.each
#puts "\n===> VERTICES COUNT = #{vertices_count}"
# FACES
#puts "===> FACES"
faces = []
lines.each do |line|
  split_line = line.split(" ")
  first_element = split_line[0]
  if first_element == 'f' then
    split_line.each_with_index do |element,i|
      if i>0 then
        c = element.split("/")
        face_number = c[0]
        faces << face_number
      end
    end
  end # if
end # lines.each
faces.each do |face|
 #print "#{face},"
end
#puts "\n===> FACES COUNT = #{faces.size}"