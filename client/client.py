from PyQt6.QtWidgets import *
from PyQt6 import QtGui, QtCore
from PyQt6 import uic
from domain.entities import *
import os
from exceptions.exceptions import *
from collections import deque


class StartWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        uic.loadUi(os.path.join("resources", "start.ui"), self)
        self.central_widget = QStackedWidget()
        self.setCentralWidget(self.central_widget)
        self.widget_queue = deque()

        self.startWidget = StartWidget(self)
        self.logInWidget = LogInWindow(self)
        self.registrationWidget = RegistrationWidget(self)

        self.http_server = HttpServer()
        self.central_widget.addWidget(self.startWidget)
        self.central_widget.addWidget(self.logInWidget)
        self.central_widget.addWidget(self.registrationWidget)
        self.central_widget.currentChanged.connect(self.resize_to_current_size)
        self.central_widget.setCurrentWidget(self.startWidget)
        self.resize_to_current_size()

    def resize_to_current_size(self):
        self.setFixedSize(self.central_widget.currentWidget().current_size)

    def show_login(self):
        self.set_new_widget(self.logInWidget)

    def show_registration(self):
        self.set_new_widget(self.registrationWidget)

    def set_new_widget(self, widget: QWidget):
        self.widget_queue.append(self.central_widget.currentWidget())
        self.central_widget.setCurrentWidget(widget)

    def pop_last_widget(self):
        self.central_widget.setCurrentWidget(self.widget_queue.pop())

    def pop_last_widget_and_remove(self):
        last_widget = self.central_widget.currentWidget()
        self.central_widget.removeWidget(last_widget)
        self.central_widget.setCurrentWidget(self.widget_queue.pop())


class StartWidget(QWidget):
    def __init__(self, parent: StartWindow):
        super().__init__(parent)
        uic.loadUi(os.path.join("resources", "start_widget.ui"), self)
        self.signInButton.clicked.connect(parent.show_login)
        self.registerButton.clicked.connect(parent.show_registration)
        self.current_size = self.size()


class LogInWindow(QWidget):
    def __init__(self, parent: StartWindow):
        super().__init__(parent)
        uic.loadUi(os.path.join("resources", "login.ui"), self)
        self.current_size = self.size()
        self.backButton.clicked.connect(parent.pop_last_widget)
        self.logButton.clicked.connect(self.log_in)
        self.menu = parent

    def log_in(self):
        try:
            user = self.menu.http_server.get_user(self.usernameField.text())
            user_menu = UserMenuWidget(self.menu, user)
            self.menu.central_widget.addWidget(user_menu)
            self.menu.set_new_widget(user_menu)
            self.usernameField.setText('')
        except (UnexpectedServerError, EntityDoesNotExist) as ex:
            err = ErrorDialog(self.menu, ex.msg)
            err.exec()
        except OSError:
            err = ErrorDialog(self.menu, "Server is unavailable!")
            err.exec()


class ErrorDialog(QMessageBox):
    def __init__(self, parent, text):
        super().__init__(parent)
        # self.setWindowTitle("Error")
        self.setText(text)
        self.setIcon(QMessageBox.Icon.Warning)
        self.setModal(True)


class RegistrationWidget(QWidget):
    def __init__(self, parent: StartWindow):
        super().__init__(parent)
        uic.loadUi(os.path.join("resources", "registration.ui"), self)
        self.menu = parent
        self.current_size = self.size()
        self.backButton.clicked.connect(parent.pop_last_widget)
        self.registerButton.clicked.connect(self.make_user)

    def make_user(self):
        try:
            user = User(
                User.make_json(self.username.text(), self.firstName.text(), self.lastName.text(), None,
                               self.country.text(),
                               self.email.text()))
            user = self.menu.http_server.post_user(user)
            user_menu = UserMenuWidget(self.menu, user)
            self.menu.central_widget.addWidget(user_menu)
            self.menu.set_new_widget(user_menu)
        except (EntityAlreadyExists, UnexpectedServerError) as ex:
            err = ErrorDialog(self.menu, ex.msg)
            err.show()
        except OSError:
            err = ErrorDialog(self.menu, "Server is unavailable!")
            err.exec()


class UserMenuWidget(QWidget):
    def __init__(self, parent: StartWindow, user: User):
        super().__init__(parent)
        uic.loadUi(os.path.join("resources", "user_menu.ui"), self)
        self.user = user
        self.menu = parent
        self.current_size = self.size()
        self.orders_widget = None
        self.game_list_widget = None
        self.charge_balance_widget = None

        self.welcome.setText("Welcome: {} {}".format(user.first_name, user.last_name))
        self.balance.setText("Your balance: {}".format(user.balance))
        self.logOut.clicked.connect(self.log_out)
        self.myOrders.clicked.connect(self.show_user_orders)
        self.makeOrder.clicked.connect(self.show_game_list)
        self.charge.clicked.connect(self.show_charge_balance)

    def update_user(self, user=None):
        if user is None:
            user = self.menu.http_server.get_user(self.user.username)
        self.balance.setText("Your balance: {}".format(user.balance))
        self.orders_widget = None
        self.game_list_widget = None

    def show_charge_balance(self):
        if self.charge_balance_widget is None:
            self.charge_balance_widget = BalanceChargeWidget(self.menu, self.user, self)
            self.menu.central_widget.addWidget(self.charge_balance_widget)
        self.menu.set_new_widget(self.charge_balance_widget)

    def show_game_list(self):
        if self.game_list_widget is None:
            self.game_list_widget = GameListWidget(self.menu, self.user, self)
            self.menu.central_widget.addWidget(self.game_list_widget)
        self.menu.set_new_widget(self.game_list_widget)

    def show_user_orders(self):
        if self.orders_widget is None:
            self.orders_widget = UserOrdersWidget(self.menu, self.user, self)
            self.menu.central_widget.addWidget(self.orders_widget)
        self.menu.set_new_widget(self.orders_widget)

    def log_out(self):
        if self.orders_widget is not None:
            self.menu.central_widget.removeWidget(self.orders_widget)
        if self.game_list_widget is not None:
            self.menu.central_widget.removeWidget(self.game_list_widget)
        if self.charge_balance_widget is not None:
            self.menu.central_widget.removeWidget(self.charge_balance_widget)
        self.menu.pop_last_widget_and_remove()
        self.menu.pop_last_widget()


class UserOrdersWidget(QWidget):
    def __init__(self, parent: StartWindow, user: User, user_menu: UserMenuWidget):
        super().__init__(parent)
        uic.loadUi(os.path.join("resources", "user_orders.ui"), self)
        self.current_size = self.size()
        self.user_menu = user_menu
        self.user = user
        self.menu = parent
        self.backButton.clicked.connect(parent.pop_last_widget)
        self.editButton.setDisabled(True)
        self.listWidget.setSelectionMode(QAbstractItemView.SelectionMode.SingleSelection)

        self.listWidget.itemSelectionChanged.connect(
            lambda: self.editButton.setDisabled(False) if self.listWidget.selectedItems()
            else self.editButton.setDisabled(True))
        self.editButton.clicked.connect(self.edit_order)
        self.set_orders()

    def edit_order(self):
        item = self.listWidget.selectedItems()[0]
        order_edit_widget = OrderEditWidget(self.menu, item.data(QtCore.Qt.ItemDataRole.UserRole), self)
        self.menu.central_widget.addWidget(order_edit_widget)
        self.menu.set_new_widget(order_edit_widget)

    def set_orders(self):
        try:
            orders = self.menu.http_server.get_user_orders(self.user.username)
            for order in orders:
                item = QListWidgetItem()
                item.setIcon(QtGui.QIcon(os.path.join("resources", "orders-icon.png")))
                item.setText("Date: {} Price: {}$".format(order.order_date, order.order_price))
                item.setData(QtCore.Qt.ItemDataRole.UserRole, order)
                self.listWidget.addItem(item)
        except UnexpectedServerError as e:
            err = ErrorDialog(self.menu, e.msg)
            err.exec()
        except OSError:
            err = ErrorDialog(self.menu, "Server is unavailable!")
            err.exec()


class GameListWidget(QWidget):
    def __init__(self, parent: StartWindow, user: User, user_menu: UserMenuWidget):
        super().__init__(parent)
        uic.loadUi(os.path.join("resources", "game_list.ui"), self)
        self.current_size = self.size()
        self.user = user
        self.menu = parent
        self.user_menu = user_menu
        self.basket = []
        self.createButton.setDisabled(True)
        self.addButton.setDisabled(True)
        self.listWidget.setSelectionMode(QAbstractItemView.SelectionMode.SingleSelection)
        self.set_games()
        self.listWidget.itemSelectionChanged.connect(
            lambda: self.addButton.setDisabled(False) if self.listWidget.selectedItems()
            else self.addButton.setDisabled(True))
        self.addButton.clicked.connect(self.add_item_to_basket)
        self.clearButton.clicked.connect(self.clear_basket)
        self.backButton.clicked.connect(self.back)
        self.createButton.clicked.connect(self.make_order)

    def back(self):
        self.clear_basket()
        self.menu.pop_last_widget()

    def clear_basket(self):
        for item in self.basket:
            self.listWidget.addItem(item)
        self.basket.clear()
        self.clearButton.setDisabled(True)
        self.createButton.setDisabled(True)

    def add_item_to_basket(self):
        item = self.listWidget.selectedItems()[0]
        self.basket.append(item)
        self.listWidget.takeItem(self.listWidget.currentRow())
        self.clearButton.setDisabled(False)
        self.createButton.setDisabled(False)

    def make_order(self):
        sold_games = []
        for item in self.basket:
            game = item.data(QtCore.Qt.ItemDataRole.UserRole)
            sold_games.append(game.id)
        order = Order(Order.make_json(None, self.user.username, None, sold_games, None, None))
        try:
            self.menu.http_server.post_order(order)
            self.user_menu.update_user()
            dlg = QMessageBox(self.menu)
            dlg.setWindowTitle("OK")
            dlg.setText("Order is under processing!")
            dlg.exec()
        except UnexpectedServerError as ex:
            err = ErrorDialog(self.menu, ex.msg)
            err.exec()
        except OSError:
            err = ErrorDialog(self.menu, "Server is unavailable!")
            err.exec()
        self.back()

    def set_games(self):
        try:
            games = self.menu.http_server.get_games()
            for game in games:
                item = QListWidgetItem()
                item.setIcon(QtGui.QIcon(os.path.join("resources", "gameIcon.png")))
                item.setText("Title: {} Price: {}$".format(game.title, game.price))
                item.setData(QtCore.Qt.ItemDataRole.UserRole, game)
                self.listWidget.addItem(item)
        except OSError:
            err = ErrorDialog(self.menu, "Server is unavailable!")
            err.exec()
            self.back()


class BalanceChargeWidget(QWidget):
    def __init__(self, parent: StartWindow, user: User, user_menu: UserMenuWidget):
        super().__init__(parent)
        uic.loadUi(os.path.join("resources", "charge.ui"), self)
        self.menu = parent
        self.user = user
        self.user_menu = user_menu
        self.current_size = self.size()
        self.backButton.clicked.connect(self.back)
        self.chargeButton.clicked.connect(self.charge_balance)

    def back(self):
        self.amountBox.setValue(0)
        self.menu.pop_last_widget()

    def charge_balance(self):
        try:
            self.menu.http_server.charge_balance(self.user, self.amountBox.value())
            self.user_menu.update_user()
        except UnexpectedServerError as ex:
            err = ErrorDialog(self.menu, ex.msg)
            err.exec()
        except OSError:
            err = ErrorDialog(self.menu, "Server is unavailable!")
            err.exec()
        self.back()


class OrderEditWidget(QWidget):
    def __init__(self, parent: StartWindow, order: Order, order_widget: UserOrdersWidget):
        super().__init__(parent)
        uic.loadUi(os.path.join("resources", "order_edit.ui"), self)
        self.order = order
        self.current_size = self.size()
        self.menu = parent
        self.order_widget = order_widget
        self.set_games()
        self.games_to_delete = []
        self.backButton.clicked.connect(self.menu.pop_last_widget_and_remove)
        self.saveButton.setDisabled(True)
        self.removeButton.setDisabled(True)
        self.listWidget.setSelectionMode(QAbstractItemView.SelectionMode.SingleSelection)

        self.listWidget.itemSelectionChanged.connect(
            lambda: self.removeButton.setDisabled(False) if self.listWidget.selectedItems()
            else self.removeButton.setDisabled(True))
        self.removeButton.clicked.connect(self.remove_game)
        self.saveButton.clicked.connect(self.save_changes)

    def save_changes(self):
        try:
            for game in self.games_to_delete:
                self.menu.http_server.remove_game_from_order(self.order.id, game.id)
            self.order_widget.listWidget.clear()
            self.order_widget.set_orders()
            self.order_widget.user_menu.update_user()
            self.menu.pop_last_widget_and_remove()
        except UnexpectedServerError as ex:
            err = ErrorDialog(self.menu, ex.msg)
            err.exec()
        except OSError:
            err = ErrorDialog(self.menu, "Server is unavailable!")
            err.exec()

    def remove_game(self):
        item = self.listWidget.selectedItems()[0]
        self.games_to_delete.append(item.data(QtCore.Qt.ItemDataRole.UserRole))
        self.listWidget.takeItem(self.listWidget.currentRow())
        self.saveButton.setDisabled(False)

    def set_games(self):
        try:
            games = self.menu.http_server.get_games()
            for game in games:
                if game.id in self.order.sold_games:
                    item = QListWidgetItem()
                    item.setIcon(QtGui.QIcon(os.path.join("resources", "gameIcon.png")))
                    item.setText("Title: {} Price: {}$".format(game.title, game.price))
                    item.setData(QtCore.Qt.ItemDataRole.UserRole, game)
                    self.listWidget.addItem(item)
        except OSError:
            err = ErrorDialog(self.menu, "Server is unavailable!")
            err.exec()


if __name__ == "__main__":
    app = QApplication([])
    window = StartWindow()
    window.show()
    app.exec()
