package com.whxy;
//负责ATM业务需求

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class ATM {
    private ArrayList<Account> accounts = new ArrayList<>();//私有
    private Scanner sc = new Scanner(System.in);
    private Account loginAccount;//记录登录账户


    //1.欢迎界面设计：
    public void start() {
        while (true) {
            System.out.println("==欢迎您进入了银行登录系统==");
            System.out.println("1.用户登录");
            System.out.println("2.用户开户");
            System.out.println("请选择您的操作：");
            int command = sc.nextInt();//用nextint来接受命令
            switch (command) {
                case 1://用户登录
                    login();
                    break;
                case 2://用户开户
                    createAccount();
                    break;
                default://没有该操作，循环
                    System.out.println("没有该操作！");
            }
        }
    }

    //用户登录功能
    private void login() {
        System.out.println("==系统登录==");
        if (accounts.size() == 0) {
            System.out.println("当前系统中无任何账户，请先开户再来登录~");
            return;//判断是否存在账户对象，如果不存在，直接登录操作
        }

        //系统中存在账户对象，可以开始登录操作
        while (true) {
            System.out.println("请您输入您的登陆卡号！");
            String cardId = sc.next();
            //判断卡号是否存在
            Account acc = getAccountCardId(cardId);
            if (acc == null) {
                System.out.println("您输入登录的卡号不存在，请您重新输入卡号：");
            } else {
                while (true) {
                    System.out.println("请您输入登录密码：");
                    String password = sc.next();
                    //判断密码是否正确
                    if (password.equals(acc.getPassword())) {
                        loginAccount = acc;
                        System.out.println("恭喜您," + acc.getUserName() + "登录成功,您的卡号是：" + acc.getCardId());
                        showUserCommand();
                        return;//登录成功，结束本次登录，返回主菜单
                    } else {
                        System.out.println("您输入的密码不正确，请重新输入：");
                    }
                }
            }
        }
    }


    //展示登陆后的操作界面
    private void showUserCommand() {
        while (true) {
            System.out.println(loginAccount.getUserName() + ",您可以选择如下功能=====");
            System.out.println("1.查询账户");
            System.out.println("2.存款");
            System.out.println("3.取款");
            System.out.println("4.转账");
            System.out.println("5.密码修改");
            System.out.println("6.退出");
            System.out.println("7.注销当前账户");
            int command = sc.nextInt();
            switch (command) {
                case 1:
                    //查询当前账户
                    showLoginAccount();
                    break;
                case 2:
                    //存款
                    depositMoney();
                    break;
                case 3:
                    //取款
                    drawMoney();
                    break;
                case 4:
                    //转账
                    transferMoney();
                    break;
                case 5:
                    //密码修改
                    modifyPassword();
                    break;
                case 6:
                    //退出
                    System.out.println(loginAccount.getUserName() + "您退出系统成功！");
                    return;
                case 7:
                    //注销当前账户
                    if (deleteAccount()) {
                        return;//注销成功后返回主菜单
                    }
                    break;
                default:
                    System.out.println("您输入的操作不存在！");
            }
        }

    }

    //转账
    private void transferMoney() {
        System.out.println("==用户转账==");
        //判断用户中是否存在其他账户
        if (accounts.size() < 2) {
            System.out.println("当前系统中仅仅一个账户！");
            return;
        }

        //转账开始
        while (true) {
            //卡号要放在循环里读，否则输错一次就会一直死循环
            System.out.println("请您输入对方的卡号：");
            String cardId = sc.next();

            //判读卡号是否正确
            Account acc = getAccountCardId(cardId);
            if (acc == null) {
                System.out.println("卡号不存在，请您重新输入！");
                continue;
            }
            if (acc == loginAccount) {
                System.out.println("您不能给自己转账，请您重新输入卡号！");
                continue;
            }

            //对方账户存在：继续认证对方姓式
            String userName = "*" + acc.getUserName().substring(1);//* + 马德华
            System.out.println("请您输入【" + userName + "】的姓氏： ");
            String preName = sc.next();
            //判断这个姓氏是否正确：
            if (!acc.getUserName().startsWith(preName)) {
                System.out.println("对不起，您输入的姓式有问题~~");
                continue;
            }

            //认证通过，开始转账
            while (true) {
                System.out.println("请您输入转账金额： ");
                double money = sc.nextDouble();
                //判断账户余额是否足够
                if (loginAccount.getBalance() >= money) {
                    //可以转账了：
                    //更新自己的账户余额
                    loginAccount.setBalance(loginAccount.getBalance() - money);
                    //更新对方的账户余额
                    acc.setBalance(acc.getBalance() + money);
                    System.out.println("转账成功，您当前的余额是：" + loginAccount.getBalance());
                    return;//用return直接跳出循环
                } else {
                    System.out.println("余额不足，无法给对方转这么多钱，最多可转：" + loginAccount.getBalance());
                }
            }
        }
    }


    private void drawMoney() {
        System.out.println("==取钱操作==");
        //判断余额是否🆗
        if (loginAccount.getBalance() < 100) {
            System.out.println("您的账户余额不足100元，不允许取钱~~");
            return;
        }

        //用户输入取款金额
        while (true) {
            System.out.println("请您输入取款金额：");
            double money = sc.nextDouble();

            if (loginAccount.getBalance() >= money) {
                //余额🆗
                //判断取款金额是否超出了限额度
                if (money > loginAccount.getLimit()) {
                    System.out.println("您当前的取款金额超出了每次限额，您每次最多可取：" + loginAccount.getLimit());
                } else {
                    //可以开始取钱了
                    loginAccount.setBalance(loginAccount.getBalance() - money);
                    System.out.println("您取款" + money + "成功，取款后剩余：" + loginAccount.getBalance());
                    return;//取款成功，返回上一级菜单
                }
            } else {
                System.out.println("余额不足，您目前的账户金额是：" + loginAccount.getBalance());
            }
        }


    }

    //存钱
    private void depositMoney() {
        System.out.println("==存钱操作==");
        System.out.println("请您输入存款金额：");
        double money = sc.nextDouble();

        //更新当前用户的余额
        loginAccount.setBalance(loginAccount.getBalance() + money);
        System.out.println("恭喜您，存钱金额：" + money + "成功，存钱后余额是：" + loginAccount.getBalance());
    }

    private void showLoginAccount() {
        System.out.println("==当前您的账户信息如下所示==");
        System.out.println("卡号：" + loginAccount.getCardId());
        System.out.println("户主：" + loginAccount.getUserName());
        System.out.println("性别：" + loginAccount.getSex());
        System.out.println("余额：" + loginAccount.getBalance());
        System.out.println("限额：" + loginAccount.getLimit());


    }

    //密码修改
    private void modifyPassword() {
        System.out.println("==密码修改==");
        while (true) {
            System.out.println("请您输入当前的密码：");
            String password = sc.next();
            //先验证当前密码是否正确
            if (!password.equals(loginAccount.getPassword())) {
                System.out.println("您输入的当前密码不正确，请重新输入！");
                continue;
            }

            //当前密码验证通过，开始设置新密码
            while (true) {
                System.out.println("请您输入新的密码：");
                String newPassword = sc.next();
                System.out.println("请您确认新的密码：");
                String okPassword = sc.next();
                if (newPassword.equals(okPassword)) {
                    loginAccount.setPassword(newPassword);
                    System.out.println("恭喜您，密码修改成功！");
                    return;//修改成功，返回上一级菜单
                } else {
                    System.out.println("您两次输入的密码不一致，请重新输入！");
                }
            }
        }
    }

    //注销当前账户
    private boolean deleteAccount() {
        System.out.println("==注销账户==");
        System.out.println("请您确认是否注销当前账户（y/n）：");
        String command = sc.next();
        switch (command) {
            case "y":
                if(loginAccount.getBalance() == 0){
                   accounts.remove(loginAccount);
                   System.out.println("您已经成功销户！");
                   return true;
                }else{
                    System.out.println("对不起，您的账户中存在金额，不允许销户~~");
                    return false;
                }
            default:
                System.out.println("好的，当前账户保留！");
                return false;
        }
    }

    /**
     * 完成用户开户操作
     **/
    private void createAccount() {
        //1.创建一个账户对象，用于封装用户的开户信息
        System.out.println("==您已经进入到了系统开户操作==");
        Account account = new Account();

        //2.用户输入自己的开户信息，赋值给账户
        System.out.println("请您输入您的用户名称：");
        String name = sc.next();
        account.setUserName(name);

        while (true) {
            System.out.println("请输入您的性别：");
            char sex = sc.next().charAt(0);//"男"
            if (sex == '男' || sex == '女') {
                account.setSex(sex);
                break;
            } else {
                System.out.println("输入有误，请重新再输！");
            }
        }

        while (true) {
            System.out.println("请您输入您的账户密码！");
            String password = sc.next();
            System.out.println("请您确认您的账户密码！");
            String okPassword = sc.next();

            //判断两次密码是否一样：
            if (password.equals(okPassword)) {
                account.setPassword(okPassword);
                break;
            } else {
                System.out.println("您输入的密码有误，请重新输入您的账户密码！");
            }
        }

        System.out.println("请您输入您的取现额度： ");
        double limit = sc.nextDouble();
        account.setLimit(limit);


        //***需要为账户自动生成一个卡号
        //返回一个八位数字的卡号”
        String newCardId = createCardId();
        account.setCardId(newCardId);

        accounts.add(account);
        System.out.println("恭喜您" + account.getUserName() + "开户成功，您的卡号是：" + account.getCardId());//封装先生或者女士
    }


    //***需要为账户自动生成一个卡号
    //返回一个八位数字的卡号的方法
    private String createCardId() {
        while (true) {//直到查到账户返回出去为止
            //定义string变量作为卡号
            String cardId = "";
            Random random = new Random();
            for (int i = 0; i < 8; i++) {
                int data = random.nextInt(10);//0~9
                cardId += data;
            }

            //判断cardid是否重复：
            Account acc = getAccountCardId(cardId);
            if (acc == null) {
                //说明cardid没有找到账户对象，因此Cardid没有与其他的重复，可以返回他作为一个新卡号
                return cardId;
            }
        }

    }

    /**
     * 根据卡号查询账户对象返回
     **/
    private Account getAccountCardId(String cardId) {
        //遍历全部的账户对象
        for (int i = 0; i < accounts.size(); i++) {
            Account acc = accounts.get(i);
            //判断卡号是否是目标卡号
            if (acc.getCardId().equals(cardId)) {
                return acc;
            }
        }

        return null;//卡号不存在

    }


}
