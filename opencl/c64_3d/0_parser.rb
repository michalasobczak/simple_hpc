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
  split_line = line.split(" ")
  first_element = split_line[0]
  if first_element == 'v' then
    x = (split_line[1].to_f*100).to_i + 50
    y = (split_line[2].to_f*100).to_i - 150
    z = (split_line[3].to_f*100).to_i
    print "#{x},#{y},#{z},\n"
    vertices_count=vertices_count+1
  end # if
end # lines.each
#puts "\n===> VERTICES COUNT = #{vertices_count}"
