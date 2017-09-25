inputstring = raw_input("number: ")
number = int (inputstring);
total = 0;
for i in range(number+1):
    if i % 3 == 0 or i % 5 == 0:
        total += i;
print("sum: " + str(total))

