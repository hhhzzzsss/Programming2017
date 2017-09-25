total = 0.0;
for k in range(1,1000000):
    total += ( (-1.0)**(k+1.0) ) / ( 2.0*k-1.0 )
total *= 4.0;
print(str(total))
