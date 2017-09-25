inputstring = raw_input("number: ")
number = int (inputstring);
total = 0;
for i in range(number+1):
    total += i;
print("sum: " + str(total))

