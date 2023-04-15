import json

import requests
from exceptions.exceptions import *


class Game:
    def __init__(self, game_json):
        self.id = game_json['id']
        self.title = game_json['title']
        self.price = game_json['price']
        self.description = game_json['description']

    @staticmethod
    def make_json(id_, title, price, description):
        game_json = {'id': id_, 'title': title, 'price': price, 'description': description}
        return game_json

    def to_json(self):
        game_json = {'id': self.id, 'title': self.title, 'price': self.price, 'description': self.description}
        return game_json


class Order:
    def __init__(self, order_json):
        self.username = order_json['username']
        self.order_date = order_json['orderDate']
        self.id = order_json['id']
        self.sold_games = order_json['soldGames']
        self.order_price = order_json['orderPrice']
        self.is_completed = order_json['completed']

    @staticmethod
    def make_json(id_, username, order_date, sold_games, is_completed, order_price):
        order_json = {'username': username, 'orderDate': order_date, 'id': id_, 'soldGames': sold_games,
                      'completed': is_completed, 'orderPrice': order_price}
        return order_json

    def to_json(self):
        order_json = {'username': self.username, 'orderDate': self.order_date, 'id': self.id,
                      'soldGames': self.sold_games,
                      'completed': self.is_completed, 'orderPrice': self.order_price}
        return order_json


class User:
    def __init__(self, user_json):
        self.username = user_json['username']
        self.first_name = user_json['firstName']
        self.last_name = user_json['lastName']
        self.balance = user_json['balance']
        self.country = user_json['country']
        self.email = user_json['email']
        print(user_json)

    @staticmethod
    def make_json(username, first_name, last_name, balance, country, email):
        user_json = {'username': username, 'firstName': first_name, 'lastName': last_name, 'balance': balance,
                     'country': country, 'email': email}
        return user_json

    def to_json(self):
        user_json = {'username': self.username, 'firstName': self.first_name, 'lastName': self.last_name,
                     'balance': self.balance,
                     'country': self.country, 'email': self.email}
        return user_json


class HttpServer:
    def __init__(self):
        self.api_url = "http://localhost:8080"

    def get_games(self):
        game_list = []
        response = requests.get(self.api_url + "/games")
        if response.status_code != 200:
            raise UnexpectedServerError(response.text)
        for game in response.json():
            game_list.append(Game(game))
        return game_list

    def get_game(self, game_id):
        response = requests.get(self.api_url + "/games/{}".format(game_id))
        if response.status_code != 200:
            if response.status_code == 404:
                raise EntityDoesNotExist("Game was not found!")
            else:
                raise UnexpectedServerError(response.text)
        return Game(response.json())

    def get_order(self, order_id):
        response = requests.get(self.api_url + "/orders/{}".format(order_id))
        if response.status_code != 200:
            if response.status_code == 404:
                raise EntityDoesNotExist("Order was not found!")
            else:
                raise UnexpectedServerError(response.text)
        return Order(response.json())

    def get_user_orders(self, username):
        response = requests.get(self.api_url + "/users/{}/orders".format(username))
        if response.status_code != 200:
            if response.status_code == 404:
                raise EntityDoesNotExist("User with username {} is not registered!".format(username))
            else:
                raise UnexpectedServerError(response.text)

        order_list = list()
        for order in response.json():
            order_list.append(Order(order))

        return order_list

    def post_order(self, order: Order):
        response = requests.post(self.api_url + "/orders", json=order.to_json())
        if response.status_code != 200:
            raise UnexpectedServerError(response.text)
        return Order(response.json())

    def get_user(self, username):
        response = requests.get(self.api_url + "/users/{}".format(username))
        if response.status_code != 200:
            if response.status_code == 404:
                raise EntityDoesNotExist("User with username {} is not registered!".format(username))
            else:
                raise UnexpectedServerError(response.text)
        return User(response.json())

    def post_user(self, user: User):
        response = requests.post(self.api_url + "/users", json=user.to_json())
        if response.status_code != 200:
            if response.status_code == 409:
                raise EntityAlreadyExists("User with username {} is already registered!".format(user.username))
            else:
                raise UnexpectedServerError(response.text)
        return User(response.json())

    def charge_balance(self, user: User, amount):
        params = {'amount': amount}
        response = requests.post(self.api_url + '/users/{}/charge'.format(user.username), params=params)
        if response.status_code != 200:
            raise UnexpectedServerError(response.text)
        return User(response.json())

    def remove_game_from_order(self, order_id, game_id):
        params = {'id': game_id}
        response = requests.patch(self.api_url + '/orders/delete/{}'.format(order_id), params=params)
        if response.status_code != 200:
            raise UnexpectedServerError(response.text)

