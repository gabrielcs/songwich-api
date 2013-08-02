# -----------------------------------------------
# MAIN
# -----------------------------------------------
# DISCLAMER :
# If you're used to Backbone.js, you may be
# confused by the absence of models, but the goal
# of this sample is to demonstrate some features
# of Play including the template engine.
# I'm not using client-side templating nor models
# for this purpose, and I do not recommend this
# behavior for real life projects.
# -----------------------------------------------

# ----------------------------------------- TASKS
class Tasks extends Backbone.View
    events:
        "click .newFolder"              : "newFolder"
        "click .list .action"           : "removeUser"
        "click .addUserList .action"    : "addUser"
    render: (project) ->
        @project = project
        # HTML is our model
        @folders = $.map $(".folder", @el), (folder) =>
            new TaskFolder
                el: $(folder)
                project: @project
    newFolder: (e) ->
        e.preventDefault()
        jsRoutes.controllers.Tasks.addFolder(@project).ajax
            context: this
            success: (tpl) ->
                newFolder = new TaskFolder
                    el: $(tpl).insertBefore(".newFolder")
                    project: @project
                newFolder.el.find("header > h3").editInPlace("edit")
             error: (err) ->
                $.error("Error: " + err)
        false
    removeUser: (e) ->
        e.preventDefault()
        jsRoutes.controllers.Projects.removeUser(@project).ajax
            context: this
            data:
                user: $(e.target).parent().data('user-id')
            success: ->
                $(e.target).parent().appendTo(".addUserList")
             error: (err) ->
                $.error("Error: " + err)
        false
    addUser: (e) ->
        e.preventDefault()
        jsRoutes.controllers.Projects.addUser(@project).ajax
            context: this
            data:
                user: $(e.target).parent().data('user-id')
            success: ->
                $(e.target).parent().appendTo(".users .list")
            error: (err) ->
                $.error("Error: " + err)
        false

# ---------------------------------- TASKS FOLDER
class TaskFolder extends Backbone.View
    events:
        "click .deleteCompleteTasks"    : "deleteCompleteTasks"
        "click .deleteAllTasks"         : "deleteAllTasks"
        "click .deleteFolder"           : "deleteFolder"
        "change header>input"           : "toggleAll"
        "submit .addTask"               : "newTask"
    initialize: (options) =>
        @project = options.project
        @tasks = $.map $(".list li",@el), (item)=>
            newTask = new TaskItem
                el: $(item)
                folder: @
            newTask.bind("change", @refreshCount)
            newTask.bind("delete", @deleteTask)
        @counter = @el.find(".counter")
        @id = @el.attr("data-folder-id")
        @name = $("header > h3", @el).editInPlace
            context: this
            onChange: @renameFolder
        @refreshCount()
    newTask: (e) =>
        e.preventDefault()
        $(document).focus() # temporary disable form
        form = $(e.target)
        taskBody = $("input[name=taskBody]", form).val()
        url = form.attr("action")
        jsRoutes.controllers.Tasks.add(@project, @id).ajax
            url: url
            type: "POST"
            context: this
            data:
                title: $("input[name=taskBody]", form).val()
                dueDate: $("input[name=dueDate]", form).val()
                assignedTo: 
                    email: $("input[name=assignedTo]", form).val()
            success: (tpl) ->
                newTask = new TaskItem(el: $(tpl), folder: @)
                @el.find("ul").append(newTask.el)
                @tasks.push(newTask)
                form.find("input[type=text]").val("").first().focus()
            error: (err) ->
                alert "Something went wrong:" + err
        false
    renameFolder: (name) =>
        @loading(true)
        jsRoutes.controllers.Tasks.renameFolder(@project, @id).ajax
            context: this
            data:
                name: name
            success: (data) ->
                @loading(false)
                @name.editInPlace("close", data)
                @el.attr("data-folder-id", data)
                @id = @el.attr("data-folder-id")
            error: (err) ->
                @loading(false)
                $.error("Error: " + err)
    deleteCompleteTasks: (e) =>
        e.preventDefault()
        $.each @tasks, (i, item) ->
            item.deleteTask() if item.el.find(".done:checked").length > 0
            true
        false
    deleteAllTasks: (e) =>
        e.preventDefault()
        $.each @tasks, (i, item)->
            item.deleteTask()
            true
        false
    deleteFolder: (e) =>
        e.preventDefault()
        @el.remove()
        false
    toggleAll: (e) =>
        val = $(e.target).is(":checked")
        $.each @tasks, (i, item) ->
            item.toggle(val)
            true
    refreshCount: =>
        count = @tasks.filter((item)->
            item.el.find(".done:checked").length == 0
        ).length
        @counter.text(count)
    deleteTask: (task) =>
        @tasks = _.without @tasks, tasks
        @refreshCount()
    loading: (display) ->
        if (display)
            @el.find("header .options").hide()
            @el.find("header .loader").show()
        else
            @el.find("header .options").show()
            @el.find("header .loader").hide()

# ------------------------------------- TASK ITEM
class TaskItem extends Backbone.View
    events:
        "change .done"          : "onToggle"
        "click .deleteTask"     : "deleteTask"
        "dblclick h4"           : "editTask"
    initialize: (options) ->
        @check = @el.find(".done")
        @id = @el.attr("data-task-id")
        @folder = options.folder
    deleteTask: (e) =>
        e.preventDefault() if e?
        @loading(false)
        jsRoutes.controllers.Tasks.delete(@id).ajax
            context: this
            data:
                name: name
            success: (data) ->
                @loading(false)
                @el.remove()
                @trigger("delete", @)
            error: (err) ->
                @loading(false)
                $.error("Error: " + err)
        false
    editTask: (e) =>
        e.preventDefault()
        # TODO
        alert "not implemented yet."
        false
    toggle: (val) =>
        @loading(true)
        jsRoutes.controllers.Tasks.update(@id).ajax
            context: this
            data:
                done: val
            success: (data) ->
                @loading(false)
                @check.attr("checked",val)
                @trigger("change", @)
            error: (err) ->
                @loading(false)
                $.error("Error: " + err)
    onToggle: (e) =>
        e.preventDefault()
        val = @check.is(":checked")
        log val
        @toggle(val)
        false
    loading: (display) ->
        if (display)
            @el.find(".delete").hide()
            @el.find(".loader").show()
        else
            @el.find(".delete").show()
            @el.find(".loader").hide()

# ------------------------------------- INIT APP
$ -> # document is ready!

    app = new AppRouter()
    drawer = new Drawer el: $("#projects")

    Backbone.history.start
        pushHistory: true

