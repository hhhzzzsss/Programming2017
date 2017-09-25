number = 2;
while True:
    prime = True
    for factor in range(2, number):
        if number%factor == 0:
            prime = False
            break
    if prime:
        print(str(number))
    number += 1;
