# Order Generator

# max 10 drinks per order

from random import randrange, seed
from datetime import date, timedelta
from menu import drinks 

# make date range
def dates(start, end) :
    for n in range(int((end - start).days)) :
        yield start + timedelta(n)

# for each order
class Order:
    order_id : str
    order_date : str
    order_time : str
    num_drinks : int
    total_cost : float
    drink1 : str
    drink2 : str
    drink3 : str
    drink4 : str
    drink5 : str
    drink6 : str
    drink7 : str
    drink8 : str   
    drink9 : str
    drink10 : str

    def __init__(self, order_id, order_date, order_time, num_drinks, drink1="0000", drink2="0000", drink3="0000", drink4="0000", drink5="0000", drink6="0000", drink7="0000", drink8="0000", drink9="0000", drink10="0000") -> None:
        self.order_id = order_id
        self.order_date = order_date
        self.order_time = order_time 
        self.num_drinks = num_drinks
        self.drink1 = drink1
        self.drink2 = drink2
        self.drink3 = drink3
        self.drink4 = drink4
        self.drink5 = drink5
        self.drink6 = drink6
        self.drink7 = drink7
        self.drink8 = drink8
        self.drink9 = drink9
        self.drink10 = drink10
        self.total_cost = 0

# random seed (so repeatable)
seed(7)

# make .csv for order generator
file = open("order_history.csv", "w") 

# headers
file.write("order_id,order_date,order_time,num_drinks,total_cost,drink1,drink2,drink3,drink4,drink5,drink6,drink7,drink8,drink9,drink10\n")

start = date(2022, 10, 1)
end = date(2023, 10, 1)

current_id = 10000001

drink_array = []

for day in dates(start, end) :
    if (day == date(2023, 8, 21) or day == date(2023, 1, 17)) :
        num_orders = randrange(190, 250)
    else :
        num_orders = randrange(75, 145)

    time = timedelta(hours=11)

    for order in range(num_orders) :
        # order id
        order_id = str(current_id)
        current_id += 1
        file.write(order_id + ",")

        # date and time
        file.write(day.strftime("%Y-%m-%d") + ",")

        mins = randrange(1,int(12/num_orders*60))
        secs = randrange(1,60)
        time = time + timedelta(minutes=mins, seconds=secs)

        file.write(str(time) + ",")

        # num drinks
        order_drinks = randrange(1, 11)
        file.write(str(order_drinks) + ",")

        drink_costs = 0

        for drink in range(order_drinks) :
            drink_num = randrange(46)
            drink_str = "00" + str(drink_num+1)

            if (drink_num+1 < 10) :
                drink_str = "0" + drink_str

            drink_array.append(drink_str)

            drink_costs += drinks[drink_str].price
        
        file.write(str(drink_costs) + ",")

        for i in range(order_drinks) :
            file.write(drink_array[i]) 
            if (i != 9) :
                file.write(",")
        for i in range(10-order_drinks) :
            file.write("0000")
            if (i != 9-order_drinks) :
                file.write(",")

        drink_array.clear()
        file.write("\n")


file.close()