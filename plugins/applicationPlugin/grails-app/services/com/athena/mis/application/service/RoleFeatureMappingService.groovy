package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.RoleFeatureMapping

class RoleFeatureMappingService extends BaseService {

    public List<RoleFeatureMapping> findAllByRoleTypeIdAndPluginId(long roleTypeId, long pluginId) {
        List<RoleFeatureMapping> lstRoleFeatureMapping = RoleFeatureMapping.findAllByRoleTypeIdAndPluginId(roleTypeId, pluginId, [readOnly: true])
        return lstRoleFeatureMapping
    }

    public boolean createRoleFeatureMapForApplication() {

        // default
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-17', pluginId: 1).save(); // url: '/appUser/checkPassword'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-92', pluginId: 1).save(); // url: '/application/renderApplicationMenu'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-17', pluginId: 1).save(); // url: '/appUser/checkPassword'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-92', pluginId: 1).save(); // url: '/application/renderApplicationMenu'

        // AppUser
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-10', pluginId: 1).save();  // url: '/appUser/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-11', pluginId: 1).save();  // url: '/appUser/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-12', pluginId: 1).save();  // url: '/appUser/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-13', pluginId: 1).save();  // url: '/appUser/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-14', pluginId: 1).save();  // url: '/appUser/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-15', pluginId: 1).save();  // url: '/appUser/list'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-154', pluginId: 1).save(); // url: '/appUser/showOnlineUser'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-155', pluginId: 1).save(); // url: '/appUser/listOnlineUser'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-156', pluginId: 1).save(); // url: '/appUser/forceLogoutOnlineUser'

        // AppUserEntity(User Entity Mapping)
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-238', pluginId: 1).save();  // url: '/appUserEntity/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-239', pluginId: 1).save();  // url: '/appUserEntity/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-240', pluginId: 1).save();  // url: '/appUserEntity/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-241', pluginId: 1).save();  // url: '/appUserEntity/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-242', pluginId: 1).save();  // url: '/appUserEntity/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-243', pluginId: 1).save();  // url: '/appUserEntity/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-244', pluginId: 1).save();  // url: '/appUserEntity/dropDownAppUserEntityReload'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'APP-244', pluginId: 1).save();  // url: '/appUserEntity/dropDownAppUserEntityReload'

        // appMail
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-187', pluginId: 1).save();  // url: '/appMail/show'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-188', pluginId: 1).save();  // url: '/appMail/update'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-189', pluginId: 1).save();  // url: '/appMail/list'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-190', pluginId: 1).save();  // url: '/appMail/select'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-251', pluginId: 1).save();  // url: '/appMail/testAppMail'

        // appShellScript
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-253', pluginId: 1).save();   // url: '/appShellScript/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-254', pluginId: 1).save();   // url: '/appShellScript/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-255', pluginId: 1).save();   // url: '/appShellScript/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-256', pluginId: 1).save();   // url: '/appShellScript/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-257', pluginId: 1).save();   // url: '/appShellScript/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-258', pluginId: 1).save();   // url: '/appShellScript/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-259', pluginId: 1).save();   // url: '/appShellScript/evaluate'

        // role
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-19', pluginId: 1).save();  // url: '/role/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-20', pluginId: 1).save();  // url: '/role/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-21', pluginId: 1).save();  // url: '/role/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-22', pluginId: 1).save();  // url: '/role/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-23', pluginId: 1).save();  // url: '/role/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-24', pluginId: 1).save();  // url: '/role/list'
        // request map
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-25', pluginId: 1).save();  // url: '/requestMap/show'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-27', pluginId: 1).save();  // url: '/requestMap/select'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-28', pluginId: 1).save();  // url: '/requestMap/update'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-31', pluginId: 1).save();  // url: '/requestMap/resetRequestMap'
        // user role
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-32', pluginId: 1).save();  // url: '/userRole/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-33', pluginId: 1).save();  // url: '/userRole/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-34', pluginId: 1).save();  // url: '/userRole/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-35', pluginId: 1).save();  // url: '/userRole/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-36', pluginId: 1).save();  // url: '/userRole/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-37', pluginId: 1).save();  // url: '/userRole/list'
        // customer
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-38', pluginId: 1).save();  // url: '/customer/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-39', pluginId: 1).save();  // url: '/customer/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-40', pluginId: 1).save();  // url: '/customer/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-41', pluginId: 1).save();  // url: '/customer/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-42', pluginId: 1).save();  // url: '/customer/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-43', pluginId: 1).save();  // url: '/customer/list'
        // employee
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-44', pluginId: 1).save();  // url: '/employee/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-45', pluginId: 1).save();  // url: '/employee/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-46', pluginId: 1).save();  // url: '/employee/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-47', pluginId: 1).save();  // url: '/employee/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-48', pluginId: 1).save();  // url: '/employee/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-49', pluginId: 1).save();  // url: '/employee/list'
        // company
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-50', pluginId: 1).save(); // url: '/company/show'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-51', pluginId: 1).save(); // url: '/company/create'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-52', pluginId: 1).save(); // url: '/company/select'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-53', pluginId: 1).save(); // url: '/company/update'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-54', pluginId: 1).save(); // url: '/company/delete'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-55', pluginId: 1).save(); // url: '/company/list'
        // project
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-56', pluginId: 1).save();  // url: '/project/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-57', pluginId: 1).save();  // url: '/project/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-58', pluginId: 1).save();  // url: '/project/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-59', pluginId: 1).save();  // url: '/project/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-60', pluginId: 1).save();  // url: '/project/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-61', pluginId: 1).save();  // url: '/project/list'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'APP-56', pluginId: 1).save();  // url: '/project/show'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'APP-57', pluginId: 1).save();  // url: '/project/create'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'APP-58', pluginId: 1).save();  // url: '/project/select'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'APP-59', pluginId: 1).save();  // url: '/project/update'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'APP-60', pluginId: 1).save();  // url: '/project/delete'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'APP-61', pluginId: 1).save();  // url: '/project/list'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'APP-56', pluginId: 1).save();  // url: '/project/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'APP-57', pluginId: 1).save();  // url: '/project/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'APP-58', pluginId: 1).save();  // url: '/project/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'APP-59', pluginId: 1).save();  // url: '/project/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'APP-60', pluginId: 1).save();  // url: '/project/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'APP-61', pluginId: 1).save();  // url: '/project/list'

        // item
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-70', pluginId: 1).save();   // url: '/item/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-157', pluginId: 1).save();  // url: '/item/listItemByItemTypeId'

        // inventory item
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-158', pluginId: 1).save();  // url:  '/item/showInventoryItem'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-159', pluginId: 1).save();  // url:  '/item/createInventoryItem'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-160', pluginId: 1).save();  // url:  '/item/updateInventoryItem'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-161', pluginId: 1).save();  // url:  '/item/deleteInventoryItem'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-162', pluginId: 1).save();  // url:  '/item/listInventoryItem'

        // non inventory item
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-163', pluginId: 1).save();  // url:  '/item/showNonInventoryItem'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-164', pluginId: 1).save();  // url:  '/item/createNonInventoryItem'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-165', pluginId: 1).save();  // url:  '/item/updateNonInventoryItem'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-166', pluginId: 1).save();  // url:  '/item/deleteNonInventoryItem'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-167', pluginId: 1).save();  // url:  '/item/listNonInventoryItem'

        // fixed asset item
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-168', pluginId: 1).save();  // url:  '/item/showFixedAssetItem'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-169', pluginId: 1).save();  // url:  '/item/createFixedAssetItem'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-170', pluginId: 1).save();  // url:  '/item/updateFixedAssetItem'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-171', pluginId: 1).save();  // url:  '/item/deleteFixedAssetItem'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-172', pluginId: 1).save();  // url:  '/item/listFixedAssetItem'

        // app group
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-74', pluginId: 1).save();  // url: '/appGroup/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-75', pluginId: 1).save();  // url: '/appGroup/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-76', pluginId: 1).save();  // url: '/appGroup/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-77', pluginId: 1).save();  // url: '/appGroup/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-78', pluginId: 1).save();  // url: '/appGroup/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-79', pluginId: 1).save();  // url: '/appGroup/list'

        // app company user
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-86', pluginId: 1).save(); // url: '/appUser/showForCompanyUser'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-87', pluginId: 1).save(); // url: '/appUser/createForCompanyUser'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-88', pluginId: 1).save(); // url: '/appUser/deleteForCompanyUser'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-89', pluginId: 1).save(); // url: '/appUser/listForCompanyUser'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-90', pluginId: 1).save(); // url: '/appUser/selectForCompanyUser'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-91', pluginId: 1).save(); // url: '/appUser/updateForCompanyUser'
        // country
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-93', pluginId: 1).save(); // url: '/country/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-94', pluginId: 1).save(); // url: '/country/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-95', pluginId: 1).save(); // url: '/country/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-96', pluginId: 1).save(); // url: '/country/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-97', pluginId: 1).save(); // url: '/country/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-98', pluginId: 1).save(); // url: '/country/list'
        // system config
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-99', pluginId: 1).save();  // url: '/systemConfiguration/show'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-100', pluginId: 1).save(); // url: '/systemConfiguration/list'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-101', pluginId: 1).save(); // url: '/systemConfiguration/select'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-102', pluginId: 1).save(); // url: '/systemConfiguration/update'
        // system entity
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-103', pluginId: 1).save(); // url: '/systemEntity/show'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-104', pluginId: 1).save(); // url: '/systemEntity/create'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-105', pluginId: 1).save(); // url: '/systemEntity/list'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-106', pluginId: 1).save(); // url: '/systemEntity/select'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-107', pluginId: 1).save(); // url: '/systemEntity/update'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-173', pluginId: 1).save(); // url: '/systemEntity/delete'
        // system entity type
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-108', pluginId: 1).save(); // url: '/systemEntityType/show'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-109', pluginId: 1).save(); // url: '/systemEntityType/list'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-110', pluginId: 1).save(); // url: '/systemEntityType/select'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-111', pluginId: 1).save(); // url: '/systemEntityType/update'
        // designation
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-118', pluginId: 1).save();  // url: '/designation/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-119', pluginId: 1).save();  // url: '/designation/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-120', pluginId: 1).save();  // url: '/designation/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-121', pluginId: 1).save();  // url: '/designation/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-122', pluginId: 1).save();  // url: '/designation/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-123', pluginId: 1).save();  // url: '/designation/select'

        // costing type
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-261', pluginId: 1).save();  // url: '/designation/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-262', pluginId: 1).save();  // url: '/designation/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-263', pluginId: 1).save();  // url: '/designation/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-264', pluginId: 1).save();  // url: '/designation/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-265', pluginId: 1).save();  // url: '/designation/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-266', pluginId: 1).save();  // url: '/designation/select'

        // theme
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-124', pluginId: 1).save(); // url: '/theme/showTheme'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-125', pluginId: 1).save(); // url: '/theme/updateTheme'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-126', pluginId: 1).save(); // url: '/theme/listTheme'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-127', pluginId: 1).save(); // url: '/theme/selectTheme'

        // sms
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-180', pluginId: 1).save(); // url: '/sms/showSms'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-181', pluginId: 1).save(); // url: '/sms/updateSms'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-182', pluginId: 1).save(); // url: '/sms/listSms'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-183', pluginId: 1).save(); // url: '/sms/selectSms'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-237', pluginId: 1).save(); // url: '/sms/sendSms'

        // currency
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-128', pluginId: 1).save(); // url: '/currency/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-129', pluginId: 1).save(); // url: '/currency/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-130', pluginId: 1).save(); // url: '/currency/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-131', pluginId: 1).save(); // url: '/currency/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-132', pluginId: 1).save(); // url: '/currency/edit'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-133', pluginId: 1).save(); // url: '/currency/delete'

        // content category
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-138', pluginId: 1).save(); // url: '/contentCategory/show'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-139', pluginId: 1).save(); // url: '/contentCategory/select'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-140', pluginId: 1).save(); // url: '/contentCategory/list'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-141', pluginId: 1).save(); // url: '/contentCategory/update'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-142', pluginId: 1).save(); // url: '/contentCategory/create'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-143', pluginId: 1).save(); // url: '/contentCategory/delete'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-153', pluginId: 1).save(); // url: '/contentCategory/listContentCategoryByContentTypeId'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'APP-153', pluginId: 1).save(); // url: '/contentCategory/listContentCategoryByContentTypeId'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'APP-153', pluginId: 1).save(); // url: '/contentCategory/listContentCategoryByContentTypeId'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-153', pluginId: 1).save(); // url: '/contentCategory/listContentCategoryByContentTypeId'

        // entity content
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'APP-144', pluginId: 1).save(); // url: '/entityContent/show'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'APP-145', pluginId: 1).save(); // url: '/entityContent/select'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'APP-146', pluginId: 1).save(); // url: '/entityContent/list'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'APP-147', pluginId: 1).save(); // url: '/entityContent/update'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'APP-148', pluginId: 1).save(); // url: '/entityContent/create'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'APP-149', pluginId: 1).save(); // url: '/entityContent/delete'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'APP-152', pluginId: 1).save(); // url: '/entityContent/downloadContent'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'APP-144', pluginId: 1).save(); // url: '/entityContent/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'APP-145', pluginId: 1).save(); // url: '/entityContent/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'APP-146', pluginId: 1).save(); // url: '/entityContent/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'APP-147', pluginId: 1).save(); // url: '/entityContent/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'APP-148', pluginId: 1).save(); // url: '/entityContent/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'APP-149', pluginId: 1).save(); // url: '/entityContent/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'APP-152', pluginId: 1).save(); // url: '/entityContent/downloadContent'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'APP-144', pluginId: 1).save();  // url: '/entityContent/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'APP-145', pluginId: 1).save();  // url: '/entityContent/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'APP-146', pluginId: 1).save();  // url: '/entityContent/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'APP-147', pluginId: 1).save();  // url: '/entityContent/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'APP-148', pluginId: 1).save();  // url: '/entityContent/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'APP-149', pluginId: 1).save();  // url: '/entityContent/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'APP-152', pluginId: 1).save();  // url: '/entityContent/downloadContent'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-144', pluginId: 1).save(); // url: '/entityContent/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-145', pluginId: 1).save(); // url: '/entityContent/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-146', pluginId: 1).save(); // url: '/entityContent/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-147', pluginId: 1).save(); // url: '/entityContent/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-148', pluginId: 1).save(); // url: '/entityContent/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-149', pluginId: 1).save(); // url: '/entityContent/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-152', pluginId: 1).save(); // url: '/entityContent/downloadContent'

        // entity note
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-245', pluginId: 1).save(); // url: '/entityNote/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-246', pluginId: 1).save(); // url: '/entityNote/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-247', pluginId: 1).save(); // url: '/entityNote/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-248', pluginId: 1).save(); // url: '/entityNote/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-249', pluginId: 1).save(); // url: '/entityNote/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-250', pluginId: 1).save(); // url: '/entityNote/delete'

        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'APP-245', pluginId: 1).save(); // url: '/entityNote/show'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'APP-246', pluginId: 1).save(); // url: '/entityNote/select'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'APP-247', pluginId: 1).save(); // url: '/entityNote/list'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'APP-248', pluginId: 1).save(); // url: '/entityNote/update'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'APP-249', pluginId: 1).save(); // url: '/entityNote/create'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'APP-250', pluginId: 1).save(); // url: '/entityNote/delete'

        //Item Type
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-174', pluginId: 1).save();  // url: '/itemType/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-175', pluginId: 1).save();  // url: '/itemType/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-176', pluginId: 1).save();  // url: '/itemType/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-177', pluginId: 1).save();  // url: '/itemType/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-178', pluginId: 1).save();  // url: '/itemType/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-179', pluginId: 1).save();  // url: '/itemType/select'

        //  bank
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-192', pluginId: 1).save();   //url: '/bank/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-193', pluginId: 1).save();   //url: '/bank/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-194', pluginId: 1).save();   //url: '/bank/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-195', pluginId: 1).save();   //url: '/bank/edit'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-196', pluginId: 1).save();   //url: '/bank/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-197', pluginId: 1).save();   //url: '/bank/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-260', pluginId: 1).save();   //url: '/bank/reloadBankDropDownTagLib'

        //  bankBranch
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-203', pluginId: 1).save();   //url: '/bankBranch/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-204', pluginId: 1).save();   //url: '/bankBranch/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-205', pluginId: 1).save();   //url: '/bankBranch/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-206', pluginId: 1).save();   //url: '/bankBranch/edit'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-207', pluginId: 1).save();   //url: '/bankBranch/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-208', pluginId: 1).save();   //url: '/bankBranch/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-209', pluginId: 1).save();   //url: '/bankBranch/reloadBranchesDropDownByBankAndDistrict'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-210', pluginId: 1).save();   //url: '/bankBranch/listDistributionPoint'

        //  district
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-211', pluginId: 1).save();  //url: '/district/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-212', pluginId: 1).save();  //url: '/district/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-213', pluginId: 1).save();  //url: '/district/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-214', pluginId: 1).save();  //url: '/district/edit'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-215', pluginId: 1).save();  //url: '/district/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-216', pluginId: 1).save();  //url: '/district/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-236', pluginId: 1).save();  //url: '/district/reloadDistrictDropDown'

        // Vehicle
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-217', pluginId: 1).save();  //url: '/vehicle/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-218', pluginId: 1).save();  //url: '/vehicle/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-219', pluginId: 1).save();  //url: '/vehicle/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-220', pluginId: 1).save();  //url: '/vehicle/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-221', pluginId: 1).save();  //url: '/vehicle/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-222', pluginId: 1).save();  //url: '/vehicle/list'

        // Supplier
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-223', pluginId: 1).save();  //url: '/supplier/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-224', pluginId: 1).save();  //url: '/supplier/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-225', pluginId: 1).save();  //url: '/supplier/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-226', pluginId: 1).save();  //url: '/supplier/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-227', pluginId: 1).save();  //url: '/supplier/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-228', pluginId: 1).save();  //url: '/supplier/list'

        // Supplier Details/Item
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-229', pluginId: 1).save();  //url: '/supplierItem/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-230', pluginId: 1).save();  //url: '/supplierItem/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-231', pluginId: 1).save();  //url: '/supplierItem/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-232', pluginId: 1).save();  //url: '/supplierItem/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-233', pluginId: 1).save();  //url: '/supplierItem/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-234', pluginId: 1).save();  //url: '/supplierItem/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-235', pluginId: 1).save();  //url: '/supplierItem/getItemListForSupplierItem'

        //reload entity note taglib
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-252', pluginId: 1).save();  //url: '/entityNote/reloadEntityNote'

        return true
    }

    public boolean createRoleFeatureMapForAccountingPlugin() {
        // default
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-157', pluginId: 2).save();     // url: '/accounting/renderAccountingMenu'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-157', pluginId: 2).save();     // url: '/accounting/renderAccountingMenu'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-157', pluginId: 2).save();     // url: '/accounting/renderAccountingMenu'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-157', pluginId: 2).save();     // url: '/accounting/renderAccountingMenu'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-157', pluginId: 2).save();     // url: '/accounting/renderAccountingMenu'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-157', pluginId: 2).save();     // url: '/accounting/renderAccountingMenu'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-157', pluginId: 2).save();    // url: '/accounting/renderAccountingMenu'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-157', pluginId: 2).save();    // url: '/accounting/renderAccountingMenu'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'ACC-157', pluginId: 2).save();    // url: '/accounting/renderAccountingMenu'

        // Acc Custom Group
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-1', pluginId: 2).save();     // url:  '/accCustomGroup/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-2', pluginId: 2).save();     // url:  '/accCustomGroup/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-3', pluginId: 2).save();     // url:  '/accCustomGroup/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-4', pluginId: 2).save();     // url:  '/accCustomGroup/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-5', pluginId: 2).save();     // url:  '/accCustomGroup/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-6', pluginId: 2).save();     // url:  '/accCustomGroup/list'

        // Acc Chart of Account
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-7', pluginId: 2).save();     // url:  '/accChartOfAccount/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-8', pluginId: 2).save();     // url:  '/accChartOfAccount/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-9', pluginId: 2).save();     // url:  '/accChartOfAccount/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-10', pluginId: 2).save();    // url:  '/accChartOfAccount/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-11', pluginId: 2).save();    // url:  '/accChartOfAccount/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-12', pluginId: 2).save();    // url:  '/accChartOfAccount/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-148', pluginId: 2).save();   // url:  '/accChartOfAccount/getSourceCategoryByAccSource'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-13', pluginId: 2).save();     // url: '/accChartOfAccount/listForVoucher'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-13', pluginId: 2).save();    // url: '/accChartOfAccount/listForVoucher'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-14', pluginId: 2).save();     // url: '/accChartOfAccount/listSourceByCoaCode'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-14', pluginId: 2).save();    // url: '/accChartOfAccount/listSourceByCoaCode'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-158', pluginId: 2).save();     // url: '/accChartOfAccount/listForVoucherByBankCashGroup'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-158', pluginId: 2).save();    // url: '/accChartOfAccount/listForVoucherByBankCashGroup'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-147', pluginId: 2).save();     // url: '/accChartOfAccount/listAccChartOfAccountByAccGroupId'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-147', pluginId: 2).save();     // url: '/accChartOfAccount/listAccChartOfAccountByAccGroupId'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-147', pluginId: 2).save();     // url: '/accChartOfAccount/listAccChartOfAccountByAccGroupId'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-147', pluginId: 2).save();    // url: '/accChartOfAccount/listAccChartOfAccountByAccGroupId'

        // Acc Group
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-15', pluginId: 2).save();     // url: '/accGroup/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-16', pluginId: 2).save();     // url: '/accGroup/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-17', pluginId: 2).save();     // url: '/accGroup/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-18', pluginId: 2).save();     // url: '/accGroup/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-19', pluginId: 2).save();     // url: '/accGroup/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-20', pluginId: 2).save();     // url: '/accGroup/list'

        // Acc Tier1
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-21', pluginId: 2).save();     // url: '/accTier1/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-22', pluginId: 2).save();     // url: '/accTier1/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-23', pluginId: 2).save();     // url: '/accTier1/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-24', pluginId: 2).save();     // url: '/accTier1/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-25', pluginId: 2).save();     // url: '/accTier1/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-26', pluginId: 2).save();     // url: '/accTier1/list'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-27', pluginId: 2).save();     // url: '/accTier1/getTier1ByAccTypeId'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-27', pluginId: 2).save();    // url: '/accTier1/getTier1ByAccTypeId'

        // Acc Tier2
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-35', pluginId: 2).save();     // url: '/accTier2/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-36', pluginId: 2).save();     // url: '/accTier2/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-37', pluginId: 2).save();     // url: '/accTier2/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-38', pluginId: 2).save();     // url: '/accTier2/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-39', pluginId: 2).save();     // url: '/accTier2/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-40', pluginId: 2).save();     // url: '/accTier2/list'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-41', pluginId: 2).save();     // url: '/accTier2/getTier2ByAccTier1Id'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-41', pluginId: 2).save();    // url: '/accTier2/getTier2ByAccTier1Id'

        // AccTier3
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-28', pluginId: 2).save();     // url: '/accTier3/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-29', pluginId: 2).save();     // url: '/accTier3/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-30', pluginId: 2).save();     // url: '/accTier3/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-31', pluginId: 2).save();     // url: '/accTier3/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-32', pluginId: 2).save();     // url: '/accTier3/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-33', pluginId: 2).save();     // url: '/accTier3/list'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-34', pluginId: 2).save();     // url: '/accTier3/getTier3ByAccTier2Id'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-34', pluginId: 2).save();    // url: '/accTier3/getTier3ByAccTier2Id'

        // Acc Type
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'ACC-206', pluginId: 2).save();     // url: '/accType/show'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'ACC-207', pluginId: 2).save();     // url: '/accType/list'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'ACC-208', pluginId: 2).save();     // url: '/accType/select'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'ACC-209', pluginId: 2).save();     // url: '/accType/update'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'ACC-252', pluginId: 2).save();     // url: '/accType/delete'

        // Acc Ipc
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-185', pluginId: 2).save();     // url: '/accIpc/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-186', pluginId: 2).save();     // url: '/accIpc/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-187', pluginId: 2).save();     // url: '/accIpc/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-188', pluginId: 2).save();     // url: '/accIpc/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-189', pluginId: 2).save();     // url: '/accIpc/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-190', pluginId: 2).save();     // url: '/accIpc/list'

        // Acc Lc
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-191', pluginId: 2).save();     // url: '/accLc/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-192', pluginId: 2).save();     // url: '/accLc/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-193', pluginId: 2).save();     // url: '/accLc/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-194', pluginId: 2).save();     // url: '/accLc/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-195', pluginId: 2).save();     // url: '/accLc/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-196', pluginId: 2).save();     // url: '/accLc/list'

        // Acc Lease Account
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-198', pluginId: 2).save();     // url: '/accLeaseAccount/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-199', pluginId: 2).save();     // url: '/accLeaseAccount/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-200', pluginId: 2).save();     // url: '/accLeaseAccount/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-201', pluginId: 2).save();     // url: '/accLeaseAccount/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-202', pluginId: 2).save();     // url: '/accLeaseAccount/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-203', pluginId: 2).save();     // url: '/accLeaseAccount/list'

        // Voucher-Type implementation
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-42', pluginId: 2).save();     // url: '/accVoucherTypeCoa/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-43', pluginId: 2).save();     // url: '/accVoucherTypeCoa/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-44', pluginId: 2).save();     // url: '/accVoucherTypeCoa/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-45', pluginId: 2).save();     // url: '/accVoucherTypeCoa/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-46', pluginId: 2).save();     // url: '/accVoucherTypeCoa/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-47', pluginId: 2).save();     // url: '/accVoucherTypeCoa/list'

        // Acc Sub-Account implementation
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-48', pluginId: 2).save();     // url: '/accSubAccount/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-49', pluginId: 2).save();     // url: '/accSubAccount/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-50', pluginId: 2).save();     // url: '/accSubAccount/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-51', pluginId: 2).save();     // url: '/accSubAccount/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-52', pluginId: 2).save();     // url: '/accSubAccount/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-53', pluginId: 2).save();     // url: '/accSubAccount/list'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-54', pluginId: 2).save();     // url: '/accSubAccount/getListByCoaId'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-54', pluginId: 2).save();    // url: '/accSubAccount/getListByCoaId'

        // Acc Division implementation
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-55', pluginId: 2).save();     // url: '/accDivision/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-56', pluginId: 2).save();     // url: '/accDivision/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-57', pluginId: 2).save();     // url: '/accDivision/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-58', pluginId: 2).save();     // url: '/accDivision/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-59', pluginId: 2).save();     // url: '/accDivision/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-60', pluginId: 2).save();     // url: '/accDivision/list'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-61', pluginId: 2).save();     // url: '/accDivision/getDivisionListByProjectId'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-61', pluginId: 2).save();    // url: '/accDivision/getDivisionListByProjectId'

        // voucher
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-62', pluginId: 2).save();     // url: '/accVoucher/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-62', pluginId: 2).save();    // url: '/accVoucher/show'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-63', pluginId: 2).save();     // url: '/accVoucher/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-63', pluginId: 2).save();    // url: '/accVoucher/create'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-251', pluginId: 2).save();    // url: '/accVoucher/cancelVoucher

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-64', pluginId: 2).save();     // url: '/accVoucher/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-64', pluginId: 2).save();    // url: '/accVoucher/select'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-65', pluginId: 2).save();     // url: '/accVoucher/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-65', pluginId: 2).save();    // url: '/accVoucher/update'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-66', pluginId: 2).save();     // url: '/accVoucher/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-66', pluginId: 2).save();    // url: '/accVoucher/list'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-67', pluginId: 2).save();    // url: '/accVoucher/postVoucher'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-68', pluginId: 2).save();     // url: '/accVoucher/showPayCash'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-68', pluginId: 2).save();    // url: '/accVoucher/showPayCash'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-69', pluginId: 2).save();     // url: '/accVoucher/listPayCash'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-69', pluginId: 2).save();    // url: '/accVoucher/listPayCash'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-70', pluginId: 2).save();     // url: '/accVoucher/showPayBank'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-70', pluginId: 2).save();    // url: '/accVoucher/showPayBank'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-71', pluginId: 2).save();     // url: '/accVoucher/listPayBank'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-71', pluginId: 2).save();    // url: '/accVoucher/listPayBank'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-72', pluginId: 2).save();     // url: '/accVoucher/showReceiveCash'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-72', pluginId: 2).save();    // url: '/accVoucher/showReceiveCash'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-73', pluginId: 2).save();     // url: '/accVoucher/listReceiveCash'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-73', pluginId: 2).save();    // url: '/accVoucher/listReceiveCash'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-74', pluginId: 2).save();     // url: '/accVoucher/showReceiveBank'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-74', pluginId: 2).save();    // url: '/accVoucher/showReceiveBank'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-75', pluginId: 2).save();     // url: '/accVoucher/listReceiveBank'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-75', pluginId: 2).save();    // url: '/accVoucher/listReceiveBank'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-76', pluginId: 2).save();     // url: '/accReport/downloadChartOfAccounts'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-76', pluginId: 2).save();     // url: '/accReport/downloadChartOfAccounts'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-76', pluginId: 2).save();     // url: '/accReport/downloadChartOfAccounts'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-76', pluginId: 2).save();    // url: '/accReport/downloadChartOfAccounts'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-152', pluginId: 2).save();     // url: '/accVoucher/listOfUnApprovedPayCash'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-152', pluginId: 2).save();    // url: '/accVoucher/listOfUnApprovedPayCash'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-153', pluginId: 2).save();     // url: '/accVoucher/listOfUnApprovedPayBank'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-153', pluginId: 2).save();    // url: '/accVoucher/listOfUnApprovedPayBank'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-154', pluginId: 2).save();     // url: '/accVoucher/listOfUnApprovedReceiveCash'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-154', pluginId: 2).save();    // url: '/accVoucher/listOfUnApprovedReceiveCash'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-155', pluginId: 2).save();     // url: '/accVoucher/listOfUnApprovedReceiveBank'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-155', pluginId: 2).save();    // url: '/accVoucher/listOfUnApprovedReceiveBank'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-156', pluginId: 2).save();     // url: '/accVoucher/listOfUnApprovedJournal'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-156', pluginId: 2).save();    // url: '/accVoucher/listOfUnApprovedJournal'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-96', pluginId: 2).save();    // url: '/accVoucher/unPostedVoucher'

        //Acc cancelled voucher
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-261', pluginId: 2).save();     // url: '/accCancelledVoucher/showCancelledVoucher'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-262', pluginId: 2).save();    // url: '/accCancelledVoucher/listCancelledVoucher'

        // voucher report
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-77', pluginId: 2).save();     // url: '/accReport/showVoucher'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-77', pluginId: 2).save();    // url: '/accReport/showVoucher'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-78', pluginId: 2).save();     // url: '/accReport/searchVoucher'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-78', pluginId: 2).save();    // url: '/accReport/searchVoucher'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-79', pluginId: 2).save();     // url: '/accReport/downloadVoucher'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-79', pluginId: 2).save();    // url: '/accReport/downloadVoucher'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-129', pluginId: 2).save();     // url: '/accReport/downloadVoucherBankCheque'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-129', pluginId: 2).save();    // url: '/accReport/downloadVoucherBankCheque'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-130', pluginId: 2).save();     // url: '/accReport/downloadVoucherBankChequePreview'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-130', pluginId: 2).save();    // url: '/accReport/downloadVoucherBankChequePreview'

        // voucher List
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-80', pluginId: 2).save();     // url: '/accReport/showVoucherList'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-80', pluginId: 2).save();    // url: '/accReport/showVoucherList'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-81', pluginId: 2).save();     // url: '/accReport/searchVoucherList'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-81', pluginId: 2).save();    // url: '/accReport/searchVoucherList'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-82', pluginId: 2).save();     // url: '/accReport/downloadVoucherList'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-82', pluginId: 2).save();    // url: '/accReport/downloadVoucherList'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-83', pluginId: 2).save();     // url: '/accReport/listForVoucherDetails'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-83', pluginId: 2).save();    // url: '/accReport/listForVoucherDetails'

        // Ledger
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-84', pluginId: 2).save();     // url: '/accReport/showLedger'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-84', pluginId: 2).save();    // url: '/accReport/showLedger'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-85', pluginId: 2).save();     // url: '/accReport/listLedger'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-85', pluginId: 2).save();    // url: '/accReport/listLedger'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-86', pluginId: 2).save();     // url: '/accReport/downloadLedger'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-86', pluginId: 2).save();    // url: '/accReport/downloadLedger'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-175', pluginId: 2).save();     // url: '/accReport/downloadLedgerCsv'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-175', pluginId: 2).save();    // url: '/accReport/downloadLedgerCsv'

        // source Ledger
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-87', pluginId: 2).save();     // url: '/accReport/showSourceLedger'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-87', pluginId: 2).save();    // url: '/accReport/showSourceLedger'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-88', pluginId: 2).save();     // url: '/accReport/listSourceLedger'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-88', pluginId: 2).save();    // url: '/accReport/listSourceLedger'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-89', pluginId: 2).save();     // url: '/accReport/downloadSourceLedger'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-89', pluginId: 2).save();    // url: '/accReport/downloadSourceLedger'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-174', pluginId: 2).save();     // url: '/accReport/downloadSourceLedgerCsv'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-174', pluginId: 2).save();    // url: '/accReport/downloadSourceLedgerCsv'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-149', pluginId: 2).save();     // url: '/accReport/listSourceByCategoryAndType'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-149', pluginId: 2).save();     // url: '/accReport/listSourceByCategoryAndType'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-149', pluginId: 2).save();    // url: '/accReport/listSourceByCategoryAndType'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-215', pluginId: 2).save();     // url: '/accReport/listSourceCategoryForSourceLedger'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-215', pluginId: 2).save();    // url: '/accReport/listSourceCategoryForSourceLedger'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-259', pluginId: 2).save();     // url: '/accReport/downloadSourceLedgeReportGroupBySource'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-259', pluginId: 2).save();    // url: '/accReport/downloadSourceLedgeReportGroupBySource'

        // Group Ledger
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-90', pluginId: 2).save();     // url: '/accReport/showForGroupLedgerRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-90', pluginId: 2).save();    // url: '/accReport/showForGroupLedgerRpt'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-91', pluginId: 2).save();     // url: '/accReport/listForGroupLedgerRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-91', pluginId: 2).save();    // url: '/accReport/listForGroupLedgerRpt'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-92', pluginId: 2).save();     // url: '/accReport/downloadForGroupLedgerRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-92', pluginId: 2).save();    // url: '/accReport/downloadForGroupLedgerRpt'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-171', pluginId: 2).save();     // url: '/accReport/downloadForGroupLedgerCsvRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-171', pluginId: 2).save();    // url: '/accReport/downloadForGroupLedgerCsvRpt'

        // trial Balance
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-216', pluginId: 2).save();     // url: '/accReport/showTrialBalanceOfLevel3'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-216', pluginId: 2).save();    // url: '/accReport/showTrialBalanceOfLevel3'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-217', pluginId: 2).save();     // url: '/accReport/listTrialBalanceOfLevel3'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-217', pluginId: 2).save();    // url: '/accReport/listTrialBalanceOfLevel3'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-218', pluginId: 2).save();     // url: '/accReport/downloadTrialBalanceOfLevel3'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-218', pluginId: 2).save();    // url: '/accReport/downloadTrialBalanceOfLevel3'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-219', pluginId: 2).save();     // url: '/accReport/downloadTrialBalanceCsvOfLevel3'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-219', pluginId: 2).save();    // url: '/accReport/downloadTrialBalanceCsvOfLevel3'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-220', pluginId: 2).save();     // url: '/accReport/showTrialBalanceOfLevel4'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-220', pluginId: 2).save();    // url: '/accReport/showTrialBalanceOfLevel4'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-221', pluginId: 2).save();     // url: '/accReport/listTrialBalanceOfLevel4'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-221', pluginId: 2).save();    // url: '/accReport/listTrialBalanceOfLevel4'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-222', pluginId: 2).save();     // url: '/accReport/downloadTrialBalanceOfLevel4'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-222', pluginId: 2).save();    // url: '/accReport/downloadTrialBalanceOfLevel4'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-223', pluginId: 2).save();     // url: '/accReport/downloadTrialBalanceCsvOfLevel4'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-223', pluginId: 2).save();    // url: '/accReport/downloadTrialBalanceCsvOfLevel4'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-224', pluginId: 2).save();     // url: '/accReport/showTrialBalanceOfLevel5'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-224', pluginId: 2).save();    // url: '/accReport/showTrialBalanceOfLevel5'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-225', pluginId: 2).save();     // url: '/accReport/listTrialBalanceOfLevel5'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-225', pluginId: 2).save();    // url: '/accReport/listTrialBalanceOfLevel5'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-226', pluginId: 2).save();     // url: '/accReport/downloadTrialBalanceOfLevel5'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-226', pluginId: 2).save();    // url: '/accReport/downloadTrialBalanceOfLevel5'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-227', pluginId: 2).save();     // url: '/accReport/downloadTrialBalanceCsvOfLevel5'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-227', pluginId: 2).save();    // url: '/accReport/downloadTrialBalanceCsvOfLevel5'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-247', pluginId: 2).save();     // url: '/accReport/showTrialBalanceOfLevel2'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-247', pluginId: 2).save();    // url: '/accReport/showTrialBalanceOfLevel2'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-248', pluginId: 2).save();     // url: '/accReport/listTrialBalanceOfLevel2'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-248', pluginId: 2).save();    // url: '/accReport/listTrialBalanceOfLevel2'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-249', pluginId: 2).save();     // url: '/accReport/downloadTrialBalanceOfLevel2'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-249', pluginId: 2).save();    // url: '/accReport/downloadTrialBalanceOfLevel2'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-250', pluginId: 2).save();     // url: '/accReport/downloadTrialBalanceCsvOfLevel2'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-250', pluginId: 2).save();    // url: '/accReport/downloadTrialBalanceCsvOfLevel2'

        // supplier payment report
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-97', pluginId: 2).save();     // url: '/accReport/showSupplierWisePayment'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-97', pluginId: 2).save();    // url: '/accReport/showSupplierWisePayment'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-98', pluginId: 2).save();     // url: '/accReport/listSupplierWisePayment'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-98', pluginId: 2).save();    // url: '/accReport/listSupplierWisePayment'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-99', pluginId: 2).save();     // url: '/accReport/downloadSupplierWisePayment'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-99', pluginId: 2).save();    // url: '/accReport/downloadSupplierWisePayment'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-173', pluginId: 2).save();     // url: '/accReport/downloadSupplierWisePaymentCsv'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-173', pluginId: 2).save();    // url: '/accReport/downloadSupplierWisePaymentCsv'

        // income Statement level 4
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-235', pluginId: 2).save();     // url: '/accReport/showIncomeStatementOfLevel4'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-235', pluginId: 2).save();    // url: '/accReport/showIncomeStatementOfLevel4'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-236', pluginId: 2).save();     // url: '/accReport/listIncomeStatementOfLevel4'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-236', pluginId: 2).save();    // url: '/accReport/listIncomeStatementOfLevel4'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-237', pluginId: 2).save();     // url: '/accReport/downloadIncomeStatementOfLevel4'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-237', pluginId: 2).save();    // url: '/accReport/downloadIncomeStatementOfLevel4'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-238', pluginId: 2).save();     // url: '/accReport/downloadIncomeStatementCsvOfLevel4'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-238', pluginId: 2).save();    // url: '/accReport/downloadIncomeStatementCsvOfLevel4'

        // income Statement level 5
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-239', pluginId: 2).save();     // url: '/accReport/showIncomeStatementOfLevel5'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-239', pluginId: 2).save();    // url: '/accReport/showIncomeStatementOfLevel5'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-240', pluginId: 2).save();     // url: '/accReport/listIncomeStatementOfLevel5'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-240', pluginId: 2).save();    // url: '/accReport/listIncomeStatementOfLevel5'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-241', pluginId: 2).save();     // url: '/accReport/downloadIncomeStatementOfLevel5'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-241', pluginId: 2).save();    // url: '/accReport/downloadIncomeStatementOfLevel5'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-242', pluginId: 2).save();     // url: '/accReport/downloadIncomeStatementCsvOfLevel5'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-242', pluginId: 2).save();    // url: '/accReport/downloadIncomeStatementCsvOfLevel5'

        // financial Statement level 5
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-210', pluginId: 2).save();     // url: '/accReport/showFinancialStatementOfLevel5'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-210', pluginId: 2).save();    // url: '/accReport/showFinancialStatementOfLevel5'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-211', pluginId: 2).save();     // url: '/accReport/listFinancialStatementOfLevel5'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-211', pluginId: 2).save();    // url: '/accReport/listFinancialStatementOfLevel5'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-212', pluginId: 2).save();     // url: '/accReport/downloadFinancialStatementOfLevel5'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-212', pluginId: 2).save();    // url: '/accReport/downloadFinancialStatementOfLevel5'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-234', pluginId: 2).save();     // url: '/accReport/downloadFinancialStatementCsvOfLevel5'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-234', pluginId: 2).save();    // url: '/accReport/downloadFinancialStatementCsvOfLevel5'

        // financial Statement level 4
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-213', pluginId: 2).save();     // url: '/accReport/showFinancialStatementOfLevel4'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-213', pluginId: 2).save();    // url: '/accReport/showFinancialStatementOfLevel4'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-214', pluginId: 2).save();     // url: '/accReport/listFinancialStatementOfLevel4'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-214', pluginId: 2).save();    // url: '/accReport/listFinancialStatementOfLevel4'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-228', pluginId: 2).save();     // url: '/accReport/downloadFinancialStatementOfLevel4'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-228', pluginId: 2).save();    // url: '/accReport/downloadFinancialStatementOfLevel4'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-233', pluginId: 2).save();     // url: '/accReport/downloadFinancialStatementCsvOfLevel4'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-233', pluginId: 2).save();    // url: '/accReport/downloadFinancialStatementCsvOfLevel4'

        // financial Statement level 3
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-229', pluginId: 2).save();     // url: '/accReport/showFinancialStatementOfLevel3'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-229', pluginId: 2).save();    // url: '/accReport/showFinancialStatementOfLevel3'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-230', pluginId: 2).save();     // url: '/accReport/listFinancialStatementOfLevel3'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-230', pluginId: 2).save();    // url: '/accReport/listFinancialStatementOfLevel3'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-231', pluginId: 2).save();     // url: '/accReport/downloadFinancialStatementOfLevel3'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-231', pluginId: 2).save();    // url: '/accReport/downloadFinancialStatementOfLevel3'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-232', pluginId: 2).save();     // url: '/accReport/downloadFinancialStatementCsvOfLevel3'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-232', pluginId: 2).save();    // url: '/accReport/downloadFinancialStatementCsvOfLevel3'

        // financial Statement level 2
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-243', pluginId: 2).save();     // url: '/accReport/showFinancialStatementOfLevel2'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-243', pluginId: 2).save();    // url: '/accReport/showFinancialStatementOfLevel2'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-244', pluginId: 2).save();     // url: '/accReport/listFinancialStatementOfLevel2'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-244', pluginId: 2).save();    // url: '/accReport/listFinancialStatementOfLevel2'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-245', pluginId: 2).save();     // url: '/accReport/downloadFinancialStatementOfLevel2'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-245', pluginId: 2).save();    // url: '/accReport/downloadFinancialStatementOfLevel2'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-246', pluginId: 2).save();     // url: '/accReport/downloadFinancialStatementCsvOfLevel2'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-246', pluginId: 2).save();    // url: '/accReport/downloadFinancialStatementCsvOfLevel2'

        // project wise expense
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-106', pluginId: 2).save();     // url: '/accReport/showProjectWiseExpense'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-106', pluginId: 2).save();    // url: '/accReport/showProjectWiseExpense'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-107', pluginId: 2).save();     // url: '/accReport/listProjectWiseExpense'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-107', pluginId: 2).save();    // url: '/accReport/listProjectWiseExpense'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-108', pluginId: 2).save();     // url: '/accReport/listProjectWiseExpenseDetails'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-108', pluginId: 2).save();    // url: '/accReport/listProjectWiseExpenseDetails'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-109', pluginId: 2).save();     // url: '/accReport/downloadProjectWiseExpense'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-109', pluginId: 2).save();    // url: '/accReport/downloadProjectWiseExpense'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-179', pluginId: 2).save();     // url: '/accReport/downloadProjectWiseExpenseCsv'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-179', pluginId: 2).save();    // url: '/accReport/downloadProjectWiseExpenseCsv'

        // project fund flow
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-254', pluginId: 2).save();     // url: '/accReport/showProjectFundFlowReport'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-254', pluginId: 2).save();    // url: '/accReport/showProjectFundFlowReport'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-255', pluginId: 2).save();     // url: '/accReport/listProjectFundFlowReport'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-255', pluginId: 2).save();    // url: '/accReport/listProjectFundFlowReport'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-253', pluginId: 2).save();    // url: '/accReport/downloadProjectFundFlowReport'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-253', pluginId: 2).save();     // url: '/accReport/downloadProjectFundFlowReport'

        // delete voucher
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-110', pluginId: 2).save();     // url: '/accVoucher/deleteVoucher'

        // source wise balance
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-111', pluginId: 2).save();     // url: '/accReport/showSourceWiseBalance'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-111', pluginId: 2).save();    // url: '/accReport/showSourceWiseBalance'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-112', pluginId: 2).save();     // url: '/accReport/listSourceWiseBalance'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-112', pluginId: 2).save();    // url: '/accReport/listSourceWiseBalance'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-113', pluginId: 2).save();     // url: '/accReport/downloadSourceWiseBalance'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-113', pluginId: 2).save();    // url: '/accReport/downloadSourceWiseBalance'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-180', pluginId: 2).save();     // url: '/accReport/downloadSourceWiseBalanceCsv'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-180', pluginId: 2).save();    // url: '/accReport/downloadSourceWiseBalanceCsv'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-205', pluginId: 2).save();     // url: '/accReport/downloadVoucherListBySourceId'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-205', pluginId: 2).save();    // url: '/accReport/downloadVoucherListBySourceId'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-260', pluginId: 2).save();     // url: '/accReport/listSourceCategoryForSourceWiseBalance'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-260', pluginId: 2).save();    // url: '/accReport/listSourceCategoryForSourceWiseBalance'

        // Acc IOU Slip Report
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-141', pluginId: 2).save();     // url: '/accReport/showAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-141', pluginId: 2).save();     // url: '/accReport/showAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-141', pluginId: 2).save();     // url: '/accReport/showAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-141', pluginId: 2).save();     // url: '/accReport/showAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-141', pluginId: 2).save();     // url: '/accReport/showAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-141', pluginId: 2).save();     // url: '/accReport/showAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-141', pluginId: 2).save();      // url: '/accReport/showAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-141', pluginId: 2).save();    // url: '/accReport/showAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-141', pluginId: 2).save();    // url: '/accReport/showAccIouSlipRpt'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-142', pluginId: 2).save();     // url: '/accReport/listAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-142', pluginId: 2).save();     // url: '/accReport/listAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-142', pluginId: 2).save();     // url: '/accReport/listAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-142', pluginId: 2).save();     // url: '/accReport/listAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-142', pluginId: 2).save();     // url: '/accReport/listAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-142', pluginId: 2).save();     // url: '/accReport/listAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-142', pluginId: 2).save();      // url: '/accReport/listAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-142', pluginId: 2).save();    // url: '/accReport/listAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-142', pluginId: 2).save();    // url: '/accReport/listAccIouSlipRpt'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-143', pluginId: 2).save();     // url: '/accReport/downloadAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-143', pluginId: 2).save();     // url: '/accReport/downloadAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-143', pluginId: 2).save();     // url: '/accReport/downloadAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-143', pluginId: 2).save();     // url: '/accReport/downloadAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-143', pluginId: 2).save();     // url: '/accReport/downloadAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-143', pluginId: 2).save();     // url: '/accReport/downloadAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-143', pluginId: 2).save();      // url: '/accReport/downloadAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-143', pluginId: 2).save();    // url: '/accReport/downloadAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-143', pluginId: 2).save();    // url: '/accReport/downloadAccIouSlipRpt'

        // For Acc-Iou-Slip
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-121', pluginId: 2).save();     // url: '/accIouSlip/show'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-121', pluginId: 2).save();     // url: '/accIouSlip/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-121', pluginId: 2).save();     // url: '/accIouSlip/show'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-121', pluginId: 2).save();     // url: '/accIouSlip/show'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-121', pluginId: 2).save();     // url: '/accIouSlip/show'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-121', pluginId: 2).save();     // url: '/accIouSlip/show'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-121', pluginId: 2).save();      // url: '/accIouSlip/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-121', pluginId: 2).save();    // url: '/accIouSlip/show'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-121', pluginId: 2).save();    // url: '/accIouSlip/show'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-122', pluginId: 2).save();     // url: '/accIouSlip/create'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-122', pluginId: 2).save();     // url: '/accIouSlip/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-122', pluginId: 2).save();     // url: '/accIouSlip/create'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-122', pluginId: 2).save();     // url: '/accIouSlip/create'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-122', pluginId: 2).save();     // url: '/accIouSlip/create'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-122', pluginId: 2).save();     // url: '/accIouSlip/create'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-122', pluginId: 2).save();      // url: '/accIouSlip/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-122', pluginId: 2).save();    // url: '/accIouSlip/create'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-122', pluginId: 2).save();    // url: '/accIouSlip/create'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-123', pluginId: 2).save();     // url: '/accIouSlip/select'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-123', pluginId: 2).save();     // url: '/accIouSlip/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-123', pluginId: 2).save();     // url: '/accIouSlip/select'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-123', pluginId: 2).save();     // url: '/accIouSlip/select'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-123', pluginId: 2).save();     // url: '/accIouSlip/select'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-123', pluginId: 2).save();     // url: '/accIouSlip/select'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-123', pluginId: 2).save();      // url: '/accIouSlip/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-123', pluginId: 2).save();    // url: '/accIouSlip/select'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-123', pluginId: 2).save();    // url: '/accIouSlip/select'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-124', pluginId: 2).save();     // url: '/accIouSlip/update'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-124', pluginId: 2).save();     // url: '/accIouSlip/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-124', pluginId: 2).save();     // url: '/accIouSlip/update'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-124', pluginId: 2).save();     // url: '/accIouSlip/update'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-124', pluginId: 2).save();     // url: '/accIouSlip/update'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-124', pluginId: 2).save();     // url: '/accIouSlip/update'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-124', pluginId: 2).save();      // url: '/accIouSlip/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-124', pluginId: 2).save();    // url: '/accIouSlip/update'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-124', pluginId: 2).save();    // url: '/accIouSlip/update'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-125', pluginId: 2).save();     // url: '/accIouSlip/delete'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-125', pluginId: 2).save();     // url: '/accIouSlip/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-125', pluginId: 2).save();     // url: '/accIouSlip/delete'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-125', pluginId: 2).save();     // url: '/accIouSlip/delete'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-125', pluginId: 2).save();     // url: '/accIouSlip/delete'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-125', pluginId: 2).save();     // url: '/accIouSlip/delete'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-125', pluginId: 2).save();      // url: '/accIouSlip/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-125', pluginId: 2).save();    // url: '/accIouSlip/delete'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-125', pluginId: 2).save();    // url: '/accIouSlip/delete'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-126', pluginId: 2).save();     // url: '/accIouSlip/list'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-126', pluginId: 2).save();     // url: '/accIouSlip/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-126', pluginId: 2).save();     // url: '/accIouSlip/list'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-126', pluginId: 2).save();     // url: '/accIouSlip/list'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-126', pluginId: 2).save();     // url: '/accIouSlip/list'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-126', pluginId: 2).save();     // url: '/accIouSlip/list'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-126', pluginId: 2).save();      // url: '/accIouSlip/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-126', pluginId: 2).save();    // url: '/accIouSlip/list'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-126', pluginId: 2).save();    // url: '/accIouSlip/list'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-127', pluginId: 2).save();     // url: '/accIouSlip/sentNotification'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-127', pluginId: 2).save();     // url: '/accIouSlip/sentNotification'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-127', pluginId: 2).save();     // url: '/accIouSlip/sentNotification'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-127', pluginId: 2).save();     // url: '/accIouSlip/sentNotification'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-127', pluginId: 2).save();     // url: '/accIouSlip/sentNotification'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-127', pluginId: 2).save();     // url: '/accIouSlip/sentNotification'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-127', pluginId: 2).save();      // url: '/accIouSlip/sentNotification'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-127', pluginId: 2).save();    // url: '/accIouSlip/sentNotification'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-127', pluginId: 2).save();    // url: '/accIouSlip/sentNotification'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-128', pluginId: 2).save();     // url: '/accIouSlip/approve'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-128', pluginId: 2).save();     // url:  '/accIouSlip/approve'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-151', pluginId: 2).save();     // url: '/accIouSlip/getIndentList'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-151', pluginId: 2).save();     // url: '/accIouSlip/getIndentList'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-151', pluginId: 2).save();     // url: '/accIouSlip/getIndentList'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-151', pluginId: 2).save();     // url: '/accIouSlip/getIndentList'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-151', pluginId: 2).save();     // url: '/accIouSlip/getIndentList'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-151', pluginId: 2).save();     // url: '/accIouSlip/getIndentList'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-151', pluginId: 2).save();      // url: '/accIouSlip/getIndentList'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-151', pluginId: 2).save();    // url: '/accIouSlip/getIndentList'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-151', pluginId: 2).save();    // url: '/accIouSlip/getIndentList'

        // For Acc-Iou-Purpose
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-131', pluginId: 2).save();     // url: '/accIouPurpose/show'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-131', pluginId: 2).save();     // url: '/accIouPurpose/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-131', pluginId: 2).save();     // url: '/accIouPurpose/show'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-131', pluginId: 2).save();     // url: '/accIouPurpose/show'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-131', pluginId: 2).save();     // url: '/accIouPurpose/show'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-131', pluginId: 2).save();     // url: '/accIouPurpose/show'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-131', pluginId: 2).save();      // url: '/accIouPurpose/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-131', pluginId: 2).save();    // url: '/accIouPurpose/show'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-131', pluginId: 2).save();    // url: '/accIouPurpose/show'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-132', pluginId: 2).save();     // url: '/accIouPurpose/create'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-132', pluginId: 2).save();     // url: '/accIouPurpose/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-132', pluginId: 2).save();     // url: '/accIouPurpose/create'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-132', pluginId: 2).save();     // url: '/accIouPurpose/create'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-132', pluginId: 2).save();     // url: '/accIouPurpose/create'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-132', pluginId: 2).save();     // url: '/accIouPurpose/create'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-132', pluginId: 2).save();      // url: '/accIouPurpose/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-132', pluginId: 2).save();    // url: '/accIouPurpose/create'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-132', pluginId: 2).save();    // url: '/accIouPurpose/create'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-133', pluginId: 2).save();     // url: '/accIouPurpose/select'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-133', pluginId: 2).save();     // url: '/accIouPurpose/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-133', pluginId: 2).save();     // url: '/accIouPurpose/select'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-133', pluginId: 2).save();     // url: '/accIouPurpose/select'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-133', pluginId: 2).save();     // url: '/accIouPurpose/select'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-133', pluginId: 2).save();     // url: '/accIouPurpose/select'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-133', pluginId: 2).save();      // url: '/accIouPurpose/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-133', pluginId: 2).save();    // url: '/accIouPurpose/select'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-133', pluginId: 2).save();    // url: '/accIouPurpose/select'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-134', pluginId: 2).save();     // url: '/accIouPurpose/update'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-134', pluginId: 2).save();     // url: '/accIouPurpose/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-134', pluginId: 2).save();     // url: '/accIouPurpose/update'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-134', pluginId: 2).save();     // url: '/accIouPurpose/update'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-134', pluginId: 2).save();     // url: '/accIouPurpose/update'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-134', pluginId: 2).save();     // url: '/accIouPurpose/update'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-134', pluginId: 2).save();      // url: '/accIouPurpose/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-134', pluginId: 2).save();    // url: '/accIouPurpose/update'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-134', pluginId: 2).save();    // url: '/accIouPurpose/update'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-135', pluginId: 2).save();     // url: '/accIouPurpose/delete'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-135', pluginId: 2).save();     // url: '/accIouPurpose/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-135', pluginId: 2).save();     // url: '/accIouPurpose/delete'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-135', pluginId: 2).save();     // url: '/accIouPurpose/delete'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-135', pluginId: 2).save();     // url: '/accIouPurpose/delete'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-135', pluginId: 2).save();     // url: '/accIouPurpose/delete'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-135', pluginId: 2).save();      // url: '/accIouPurpose/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-135', pluginId: 2).save();    // url: '/accIouPurpose/delete'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-135', pluginId: 2).save();    // url: '/accIouPurpose/delete'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-136', pluginId: 2).save();     // url: '/accIouPurpose/list'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-136', pluginId: 2).save();     // url: '/accIouPurpose/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-136', pluginId: 2).save();     // url: '/accIouPurpose/list'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-136', pluginId: 2).save();     // url: '/accIouPurpose/list'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-136', pluginId: 2).save();     // url: '/accIouPurpose/list'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-136', pluginId: 2).save();     // url: '/accIouPurpose/list'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-136', pluginId: 2).save();      // url: '/accIouPurpose/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-136', pluginId: 2).save();    // url: '/accIouPurpose/list'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-136', pluginId: 2).save();    // url: '/accIouPurpose/list'

        // acc financial year
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-114', pluginId: 2).save();     // url: '/accFinancialYear/show'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-114', pluginId: 2).save();     // url: '/accFinancialYear/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-114', pluginId: 2).save();     // url: '/accFinancialYear/show'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-114', pluginId: 2).save();     // url: '/accFinancialYear/show'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-114', pluginId: 2).save();     // url: '/accFinancialYear/show'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-114', pluginId: 2).save();     // url: '/accFinancialYear/show'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-114', pluginId: 2).save();      // url: '/accFinancialYear/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-114', pluginId: 2).save();    // url: '/accFinancialYear/show'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-114', pluginId: 2).save();    // url: '/accFinancialYear/show'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-115', pluginId: 2).save();     // url: '/accFinancialYear/list'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-115', pluginId: 2).save();     // url: '/accFinancialYear/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-115', pluginId: 2).save();     // url: '/accFinancialYear/list'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-115', pluginId: 2).save();     // url: '/accFinancialYear/list'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-115', pluginId: 2).save();     // url: '/accFinancialYear/list'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-115', pluginId: 2).save();     // url: '/accFinancialYear/list'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-115', pluginId: 2).save();      // url: '/accFinancialYear/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-115', pluginId: 2).save();    // url: '/accFinancialYear/list'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-115', pluginId: 2).save();    // url: '/accFinancialYear/list'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-116', pluginId: 2).save();     // url: '/accFinancialYear/create'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-116', pluginId: 2).save();     // url: '/accFinancialYear/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-116', pluginId: 2).save();     // url: '/accFinancialYear/create'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-116', pluginId: 2).save();     // url: '/accFinancialYear/create'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-116', pluginId: 2).save();     // url: '/accFinancialYear/create'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-116', pluginId: 2).save();     // url: '/accFinancialYear/create'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-116', pluginId: 2).save();      // url: '/accFinancialYear/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-116', pluginId: 2).save();    // url: '/accFinancialYear/create'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-116', pluginId: 2).save();    // url: '/accFinancialYear/create'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-117', pluginId: 2).save();     // url: '/accFinancialYear/update'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-117', pluginId: 2).save();     // url: '/accFinancialYear/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-117', pluginId: 2).save();     // url: '/accFinancialYear/update'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-117', pluginId: 2).save();     // url: '/accFinancialYear/update'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-117', pluginId: 2).save();     // url: '/accFinancialYear/update'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-117', pluginId: 2).save();     // url: '/accFinancialYear/update'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-117', pluginId: 2).save();      // url: '/accFinancialYear/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-117', pluginId: 2).save();    // url: '/accFinancialYear/update'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-117', pluginId: 2).save();    // url: '/accFinancialYear/update'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-118', pluginId: 2).save();     // url: '/accFinancialYear/delete'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-118', pluginId: 2).save();     // url: '/accFinancialYear/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-118', pluginId: 2).save();     // url: '/accFinancialYear/delete'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-118', pluginId: 2).save();     // url: '/accFinancialYear/delete'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-118', pluginId: 2).save();     // url: '/accFinancialYear/delete'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-118', pluginId: 2).save();     // url: '/accFinancialYear/delete'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-118', pluginId: 2).save();      // url: '/accFinancialYear/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-118', pluginId: 2).save();    // url: '/accFinancialYear/delete'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-118', pluginId: 2).save();    // url: '/accFinancialYear/delete'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'ACC-119', pluginId: 2).save();     // url: '/accFinancialYear/select'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-119', pluginId: 2).save();     // url: '/accFinancialYear/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-119', pluginId: 2).save();     // url: '/accFinancialYear/select'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'ACC-119', pluginId: 2).save();     // url: '/accFinancialYear/select'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-119', pluginId: 2).save();     // url: '/accFinancialYear/select'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'ACC-119', pluginId: 2).save();     // url: '/accFinancialYear/select'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'ACC-119', pluginId: 2).save();      // url: '/accFinancialYear/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-119', pluginId: 2).save();    // url: '/accFinancialYear/select'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'ACC-119', pluginId: 2).save();    // url: '/accFinancialYear/select'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-150', pluginId: 2).save();     // url: '/accFinancialYear/setCurrentFinancialYear'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-150', pluginId: 2).save();     // url: '/accFinancialYear/setCurrentFinancialYear'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-150', pluginId: 2).save();    // url: '/accFinancialYear/setCurrentFinancialYear'

        // supplier payable report
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-137', pluginId: 2).save();     // url: '/accReport/showSupplierWisePayable'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-137', pluginId: 2).save();    // url: '/accReport/showSupplierWisePayable'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-138', pluginId: 2).save();     // url: '/accReport/listSupplierWisePayable'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-138', pluginId: 2).save();    // url: '/accReport/listSupplierWisePayable'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-139', pluginId: 2).save();     // url: '/accReport/downloadSupplierWisePayable'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-139', pluginId: 2).save();    // url: '/accReport/downloadSupplierWisePayable'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-172', pluginId: 2).save();     // url: '/accReport/downloadSupplierWisePayableCsv'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-172', pluginId: 2).save();    // url: '/accReport/downloadSupplierWisePayableCsv'

        // Bank Statement
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-166', pluginId: 2).save();     // url: '/accBankStatement/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-166', pluginId: 2).save();     // url: '/accBankStatement/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-166', pluginId: 2).save();    // url: '/accBankStatement/show'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-167', pluginId: 2).save();     // url: '/accBankStatement/uploadBankStatementFile'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-167', pluginId: 2).save();     // url: '/accBankStatement/uploadBankStatementFile'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-167', pluginId: 2).save();    // url: '/accBankStatement/uploadBankStatementFile'

        // trial Balance
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-168', pluginId: 2).save();     // url: '/accReport/showCustomGroupBalance'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-168', pluginId: 2).save();    // url: '/accReport/showCustomGroupBalance'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-169', pluginId: 2).save();     // url: '/accReport/listCustomGroupBalance'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-169', pluginId: 2).save();    // url: '/accReport/listCustomGroupBalance'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-170', pluginId: 2).save();     // url: '/accReport/downloadCustomGroupBalance'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-170', pluginId: 2).save();    // url: '/accReport/downloadCustomGroupBalance'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'ACC-176', pluginId: 2).save();     // url: '/accReport/downloadCustomGroupBalanceCsv'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-176', pluginId: 2).save();    // url: '/accReport/downloadCustomGroupBalanceCsv'

        // trial Balance Report
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-144', pluginId: 2).save();     // url: '/accReport/showBankReconciliationCheque'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-144', pluginId: 2).save();     // url: '/accReport/showBankReconciliationCheque'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-144', pluginId: 2).save();    // url: '/accReport/showBankReconciliationCheque'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-145', pluginId: 2).save();     // url: '/accReport/listBankReconciliationCheque'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-145', pluginId: 2).save();     // url: '/accReport/listBankReconciliationCheque'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-145', pluginId: 2).save();    // url: '/accReport/listBankReconciliationCheque'

        // bank cheque reconciliation
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-146', pluginId: 2).save();     // url: '/accReport/downloadBankReconciliationCheque'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-146', pluginId: 2).save();     // url: '/accReport/downloadBankReconciliationCheque'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-146', pluginId: 2).save();    // url: '/accReport/downloadBankReconciliationCheque'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'ACC-178', pluginId: 2).save();     // url: '/accReport/downloadBankReconciliationChequeCsv'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'ACC-178', pluginId: 2).save();     // url: '/accReport/downloadBankReconciliationChequeCsv'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'ACC-178', pluginId: 2).save();    // url: '/accReport/downloadBankReconciliationChequeCsv'

        return true
    }

    public boolean createRoleFeatureMapForBudgetPlugin() {
        // default
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'BUDG-38', pluginId: 3).save();   //   url: '/budgBudget/renderBudgetMenu'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-38', pluginId: 3).save();   //   url: '/budgBudget/renderBudgetMenu'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-38', pluginId: 3).save();   //   url: '/budgBudget/renderBudgetMenu'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'BUDG-38', pluginId: 3).save();   //   url: '/budgBudget/renderBudgetMenu'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-38', pluginId: 3).save();  //   url: '/budgBudget/renderBudgetMenu'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'BUDG-38', pluginId: 3).save();  //   url: '/budgBudget/renderBudgetMenu'

        // BOQ implementation
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-1', pluginId: 3).save();    //   url: '/budgBudget/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-1', pluginId: 3).save();    //   url: '/budgBudget/show'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'BUDG-1', pluginId: 3).save();    //   url: '/budgBudget/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-1', pluginId: 3).save();   //   url: '/budgBudget/show'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-2', pluginId: 3).save();    //   url: '/budgBudget/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-2', pluginId: 3).save();    //   url: '/budgBudget/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-2', pluginId: 3).save();   //   url: '/budgBudget/create'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-3', pluginId: 3).save();    //   url: '/budgBudget/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-3', pluginId: 3).save();    //   url: '/budgBudget/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-3', pluginId: 3).save();   //   url: '/budgBudget/select'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-4', pluginId: 3).save();    //   url: '/budgBudget/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-4', pluginId: 3).save();    //   url: '/budgBudget/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-4', pluginId: 3).save();   //   url: '/budgBudget/update'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-5', pluginId: 3).save();    //   url: '/budgBudget/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-5', pluginId: 3).save();    //   url: '/budgBudget/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-5', pluginId: 3).save();   //   url: '/budgBudget/delete'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-6', pluginId: 3).save();    //   url: '/budgBudget/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-6', pluginId: 3).save();    //   url: '/budgBudget/list'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'BUDG-6', pluginId: 3).save();    //   url: '/budgBudget/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-6', pluginId: 3).save();   //   url: '/budgBudget/list'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'BUDG-8', pluginId: 3).save();    //   url: '/budgBudget/getBudgetGridByProject'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-8', pluginId: 3).save();    //   url: '/budgBudget/getBudgetGridByProject'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-8', pluginId: 3).save();    //   url: '/budgBudget/getBudgetGridByProject'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'BUDG-8', pluginId: 3).save();    //   url: '/budgBudget/getBudgetGridByProject'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'BUDG-8', pluginId: 3).save();    //   url: '/budgBudget/getBudgetGridByProject'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'BUDG-8', pluginId: 3).save();    //   url: '/budgBudget/getBudgetGridByProject'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-8', pluginId: 3).save();   //   url: '/budgBudget/getBudgetGridByProject'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'BUDG-8', pluginId: 3).save();   //   url: '/budgBudget/getBudgetGridByProject'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'BUDG-29', pluginId: 3).save();    //   url: '/budgBudget/getBudgetGridByInventory'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-29', pluginId: 3).save();    //   url: '/budgBudget/getBudgetGridByInventory'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-29', pluginId: 3).save();    //   url: '/budgBudget/getBudgetGridByInventory'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'BUDG-29', pluginId: 3).save();    //   url: '/budgBudget/getBudgetGridByInventory'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'BUDG-29', pluginId: 3).save();    //   url: '/budgBudget/getBudgetGridByInventory'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-29', pluginId: 3).save();   //   url: '/budgBudget/getBudgetGridByInventory'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-30', pluginId: 3).save();    //   url: '/budgBudget/getBudgetListForQs'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-30', pluginId: 3).save();    //   url: '/budgBudget/getBudgetListForQs'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-37', pluginId: 3).save();    //   url: '/budgBudget/getBudgetStatusForDashBoard'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-37', pluginId: 3).save();    //   url: '/budgBudget/getBudgetStatusForDashBoard'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-37', pluginId: 3).save();   //   url: '/budgBudget/getBudgetStatusForDashBoard'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-83', pluginId: 3).save();    //   url: '/budgBudget/getBudgetGridForSprint'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-83', pluginId: 3).save();    //   url: '/budgBudget/getBudgetGridForSprint'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-83', pluginId: 3).save();   //   url: '/budgBudget/getBudgetGridForSprint'

        // Budg Task
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-54', pluginId: 3).save();    //   url: '/budgTask/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-54', pluginId: 3).save();    //   url: '/budgTask/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-54', pluginId: 3).save();   //   url: '/budgTask/show'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-55', pluginId: 3).save();    //   url: '/budgTask/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-55', pluginId: 3).save();    //   url: '/budgTask/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-55', pluginId: 3).save();   //   url: '/budgTask/create'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-56', pluginId: 3).save();    //   url: '/budgTask/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-56', pluginId: 3).save();    //   url: '/budgTask/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-56', pluginId: 3).save();   //   url: '/budgTask/select'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-57', pluginId: 3).save();    //   url: '/budgTask/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-57', pluginId: 3).save();    //   url: '/budgTask/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-57', pluginId: 3).save();   //   url: '/budgTask/update'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-58', pluginId: 3).save();    //   url: '/budgTask/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-58', pluginId: 3).save();    //   url: '/budgTask/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-58', pluginId: 3).save();   //   url: '/budgTask/delete'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-59', pluginId: 3).save();    //   url: '/budgTask/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-59', pluginId: 3).save();    //   url: '/budgTask/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-59', pluginId: 3).save();   //   url: '/budgTask/list'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-85', pluginId: 3).save();    //   url: '/budgTask/showTaskForSprint'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-85', pluginId: 3).save();    //   url: '/budgTask/showTaskForSprint'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-85', pluginId: 3).save();   //   url: '/budgTask/showTaskForSprint'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-88', pluginId: 3).save();    //   url: '/budgTask/listTaskForSprint'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-88', pluginId: 3).save();    //   url: '/budgTask/listTaskForSprint'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-88', pluginId: 3).save();   //   url: '/budgTask/listTaskForSprint'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-89', pluginId: 3).save();    //   url: '/budgTask/updateTaskForSprint'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-89', pluginId: 3).save();    //   url: '/budgTask/updateTaskForSprint'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-89', pluginId: 3).save();   //   url: '/budgTask/updateTaskForSprint'

        // Budget Details
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-9', pluginId: 3).save();   //   url: '/budgBudgetDetails/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-9', pluginId: 3).save();   //   url: '/budgBudgetDetails/show'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'BUDG-9', pluginId: 3).save();   //   url: '/budgBudgetDetails/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-9', pluginId: 3).save();   //   url: '/budgBudgetDetails/show'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-10', pluginId: 3).save();   //   url: '/budgBudgetDetails/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-10', pluginId: 3).save();   //   url: '/budgBudgetDetails/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-10', pluginId: 3).save();   //   url: '/budgBudgetDetails/create'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-11', pluginId: 3).save();   //   url: '/budgBudgetDetails/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-11', pluginId: 3).save();   //   url: '/budgBudgetDetails/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-11', pluginId: 3).save();   //   url: '/budgBudgetDetails/select'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-12', pluginId: 3).save();   //   url: '/budgBudgetDetails/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-12', pluginId: 3).save();   //   url: '/budgBudgetDetails/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-12', pluginId: 3).save();   //   url: '/budgBudgetDetails/update'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-13', pluginId: 3).save();   //   url: '/budgBudgetDetails/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-13', pluginId: 3).save();   //   url: '/budgBudgetDetails/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-13', pluginId: 3).save();   //   url: '/budgBudgetDetails/delete'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-14', pluginId: 3).save();   //   url: '/budgBudgetDetails/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-14', pluginId: 3).save();   //   url: '/budgBudgetDetails/list'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'BUDG-14', pluginId: 3).save();   //   url: '/budgBudgetDetails/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-14', pluginId: 3).save();   //   url: '/budgBudgetDetails/list'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-15', pluginId: 3).save();   //   url: '/budgBudgetDetails/getItemListBudgetDetails'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-15', pluginId: 3).save();   //   url: '/budgBudgetDetails/getItemListBudgetDetails'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-15', pluginId: 3).save();   //   url: '/budgBudgetDetails/getItemListBudgetDetails'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-90', pluginId: 3).save();   //   url: '/budgBudgetDetails/generateBudgetRequirement'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-90', pluginId: 3).save();   //   url: '/budgBudgetDetails/generateBudgetRequirement'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-90', pluginId: 3).save();   //   url: '/budgBudgetDetails/generateBudgetRequirement'

        // Budget Schema
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-60', pluginId: 3).save();   //   url: '/budgSchema/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-60', pluginId: 3).save();   //   url: '/budgSchema/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-60', pluginId: 3).save();   //   url: '/budgSchema/show'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-61', pluginId: 3).save();   //   url: '/budgSchema/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-61', pluginId: 3).save();   //   url: '/budgSchema/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-61', pluginId: 3).save();   //   url: '/budgSchema/create'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-62', pluginId: 3).save();   //   url: '/budgSchema/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-62', pluginId: 3).save();   //   url: '/budgSchema/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-62', pluginId: 3).save();   //   url: '/budgSchema/select'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-63', pluginId: 3).save();   //   url: '/budgSchema/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-63', pluginId: 3).save();   //   url: '/budgSchema/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-63', pluginId: 3).save();   //   url: '/budgSchema/update'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-64', pluginId: 3).save();   //   url: '/budgSchema/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-64', pluginId: 3).save();   //   url: '/budgSchema/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-64', pluginId: 3).save();   //   url: '/budgSchema/delete'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-65', pluginId: 3).save();   //   url: '/budgSchema/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-65', pluginId: 3).save();   //   url: '/budgSchema/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-65', pluginId: 3).save();   //   url: '/budgSchema/list'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-66', pluginId: 3).save();   //   url: '/budgSchema/listItemForBudgetSchema'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-66', pluginId: 3).save();   //   url: '/budgSchema/listItemForBudgetSchema'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-66', pluginId: 3).save();  //   url: '/budgSchema/listItemForBudgetSchema'

        // sprint
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-67', pluginId: 3).save();   //   url: '/budgSprint/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-67', pluginId: 3).save();   //   url: '/budgSprint/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-67', pluginId: 3).save();   //   url: '/budgSprint/show'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-68', pluginId: 3).save();   //   url: '/budgSprint/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-68', pluginId: 3).save();   //   url: '/budgSprint/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-68', pluginId: 3).save();   //   url: '/budgSprint/create'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-69', pluginId: 3).save();   //   url: '/budgSprint/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-69', pluginId: 3).save();   //   url: '/budgSprint/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-69', pluginId: 3).save();   //   url: '/budgSprint/select'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-70', pluginId: 3).save();   //   url: '/budgSprint/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-70', pluginId: 3).save();   //   url: '/budgSprint/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-70', pluginId: 3).save();   //   url: '/budgSprint/update'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-71', pluginId: 3).save();   //   url: '/budgSprint/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-71', pluginId: 3).save();   //   url: '/budgSprint/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-71', pluginId: 3).save();   //   url: '/budgSprint/delete'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-72', pluginId: 3).save();   //   url: '/budgSprint/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-72', pluginId: 3).save();   //   url: '/budgSprint/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-72', pluginId: 3).save();   //   url: '/budgSprint/list'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-84', pluginId: 3).save();   //   url: '/budgSprint/setCurrentBudgSprint'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-84', pluginId: 3).save();   //   url: '/budgSprint/setCurrentBudgSprint'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-84', pluginId: 3).save();   //   url: '/budgSprint/setCurrentBudgSprint'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-86', pluginId: 3).save();   //   url: '/budgSprint/showForCurrentSprint'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-86', pluginId: 3).save();   //   url: '/budgSprint/showForCurrentSprint'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-86', pluginId: 3).save();   //   url: '/budgSprint/showForCurrentSprint'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-87', pluginId: 3).save();   //   url: '/budgSprint/listForCurrentSprint'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-87', pluginId: 3).save();   //   url: '/budgSprint/listForCurrentSprint'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-87', pluginId: 3).save();   //   url: '/budgSprint/listForCurrentSprint'

        // sprint project
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-73', pluginId: 3).save();   //   url: '/budgSprintBudget/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-73', pluginId: 3).save();   //   url: '/budgSprintBudget/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-73', pluginId: 3).save();   //   url: '/budgSprintBudget/show'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-74', pluginId: 3).save();   //   url: '/budgSprintBudget/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-74', pluginId: 3).save();   //   url: '/budgSprintBudget/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-74', pluginId: 3).save();   //   url: '/budgSprintBudget/create'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-75', pluginId: 3).save();   //   url: '/budgSprintBudget/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-75', pluginId: 3).save();   //   url: '/budgSprintBudget/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-75', pluginId: 3).save();   //   url: '/budgSprintBudget/select'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-76', pluginId: 3).save();   //   url: '/budgSprintBudget/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-76', pluginId: 3).save();   //   url: '/budgSprintBudget/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-76', pluginId: 3).save();   //   url: '/budgSprintBudget/update'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-77', pluginId: 3).save();   //   url: '/budgSprintBudget/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-77', pluginId: 3).save();   //   url: '/budgSprintBudget/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-77', pluginId: 3).save();   //   url: '/budgSprintBudget/delete'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-78', pluginId: 3).save();   //   url: '/budgSprintBudget/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-78', pluginId: 3).save();   //   url: '/budgSprintBudget/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-78', pluginId: 3).save();   //   url: '/budgSprintBudget/list'

        // Budget Scope
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'BUDG-16', pluginId: 3).save();    //   url: '/budgetScope/show'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-16', pluginId: 3).save();    //   url: '/budgetScope/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-16', pluginId: 3).save();    //   url: '/budgetScope/show'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'BUDG-17', pluginId: 3).save();    //   url: '/budgetScope/create'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-17', pluginId: 3).save();    //   url: '/budgetScope/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-17', pluginId: 3).save();    //   url: '/budgetScope/create'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'BUDG-18', pluginId: 3).save();    //   url: '/budgetScope/select'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-18', pluginId: 3).save();    //   url: '/budgetScope/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-18', pluginId: 3).save();    //   url: '/budgetScope/select'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'BUDG-19', pluginId: 3).save();    //   url: '/budgetScope/update'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-19', pluginId: 3).save();    //   url: '/budgetScope/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-19', pluginId: 3).save();    //   url: '/budgetScope/update'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'BUDG-20', pluginId: 3).save();    //   url: '/budgetScope/delete'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-20', pluginId: 3).save();    //   url: '/budgetScope/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-20', pluginId: 3).save();    //   url: '/budgetScope/delete'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'BUDG-21', pluginId: 3).save();    //   url: '/budgetScope/list'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-21', pluginId: 3).save();    //   url: '/budgetScope/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-21', pluginId: 3).save();    //   url: '/budgetScope/list'

        // Project Budget Scope
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'BUDG-22', pluginId: 3).save();    //   url: '/projectBudgetScope/show'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-22', pluginId: 3).save();    //   url: '/projectBudgetScope/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-22', pluginId: 3).save();    //   url: '/projectBudgetScope/show'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'BUDG-23', pluginId: 3).save();    //   url: '/projectBudgetScope/select'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-23', pluginId: 3).save();    //   url: '/projectBudgetScope/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-23', pluginId: 3).save();    //   url: '/projectBudgetScope/select'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'BUDG-24', pluginId: 3).save();    //   url: '/projectBudgetScope/update'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-24', pluginId: 3).save();    //   url: '/projectBudgetScope/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-24', pluginId: 3).save();    //   url: '/projectBudgetScope/update'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'BUDG-25', pluginId: 3).save();    //   url: '/projectBudgetScope/getBudgetScope'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-25', pluginId: 3).save();    //   url: '/projectBudgetScope/getBudgetScope'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-25', pluginId: 3).save();    //   url: '/projectBudgetScope/getBudgetScope'

        // Report
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-26', pluginId: 3).save();    //   url: '/budgReport/showBudgetRpt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-26', pluginId: 3).save();    //   url: '/budgReport/showBudgetRpt'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'BUDG-26', pluginId: 3).save();    //   url: '/budgReport/showBudgetRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-26', pluginId: 3).save();   //   url: '/budgReport/showBudgetRpt'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-27', pluginId: 3).save();    //   url: '/budgReport/searchBudgetRpt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-27', pluginId: 3).save();    //   url: '/budgReport/searchBudgetRpt'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'BUDG-27', pluginId: 3).save();    //   url: '/budgReport/searchBudgetRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-27', pluginId: 3).save();   //   url: '/budgReport/searchBudgetRpt'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-28', pluginId: 3).save();    //   url: '/budgReport/downloadBudgetRpt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-28', pluginId: 3).save();    //   url: '/budgReport/downloadBudgetRpt'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'BUDG-28', pluginId: 3).save();    //   url: '/budgReport/downloadBudgetRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-28', pluginId: 3).save();   //   url: '/budgReport/downloadBudgetRpt'

        // Project status
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-31', pluginId: 3).save();    //   url: '/budgReport/showProjectStatus'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-31', pluginId: 3).save();    //   url: '/budgReport/showProjectStatus'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-32', pluginId: 3).save();    //   url: '/budgReport/searchProjectStatus'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-32', pluginId: 3).save();    //   url: '/budgReport/searchProjectStatus'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-36', pluginId: 3).save();    //   url: '/budgReport/downloadProjectStatus'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-36', pluginId: 3).save();    //   url: '/budgReport/downloadProjectStatus'

        // Project Costing
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-51', pluginId: 3).save();    //   url: '/budgReport/listProjectCosting'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-52', pluginId: 3).save();    //   url: '/budgReport/downloadProjectCosting'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-53', pluginId: 3).save();    //   url: '/budgReport/showProjectCosting'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-51', pluginId: 3).save();    //   url: '/budgReport/listProjectCosting'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-52', pluginId: 3).save();    //   url: '/budgReport/downloadProjectCosting'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-53', pluginId: 3).save();    //   url: '/budgReport/showProjectCosting'

        // Consumption deviation
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-40', pluginId: 3).save();    //   url: '/budgReport/showConsumptionDeviation'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-40', pluginId: 3).save();    //   url: '/budgReport/showConsumptionDeviation'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-40', pluginId: 3).save();   //   url: '/budgReport/showConsumptionDeviation'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-41', pluginId: 3).save();    //   url: '/budgReport/listConsumptionDeviation'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-41', pluginId: 3).save();    //   url: '/budgReport/listConsumptionDeviation'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-41', pluginId: 3).save();   //   url: '/budgReport/listConsumptionDeviation'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-42', pluginId: 3).save();    //   url: '/budgReport/downloadConsumptionDeviation'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-42', pluginId: 3).save();    //   url: '/budgReport/downloadConsumptionDeviation'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-42', pluginId: 3).save();   //   url: '/budgReport/downloadConsumptionDeviation'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-43', pluginId: 3).save();    //   url: '/budgReport/downloadConsumptionDeviationCsv'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-43', pluginId: 3).save();    //   url: '/budgReport/downloadConsumptionDeviationCsv'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-43', pluginId: 3).save();   //   url: '/budgReport/downloadConsumptionDeviationCsv'

        // budget sprint
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-80', pluginId: 3).save();    //   url: '/budgReport/downloadSprintReport'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-80', pluginId: 3).save();    //   url: '/budgReport/downloadSprintReport'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-80', pluginId: 3).save();    //   url: '/budgReport/downloadSprintReport'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-81', pluginId: 3).save();    //   url: '/budgReport/showBudgetSprint'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-81', pluginId: 3).save();    //   url: '/budgReport/showBudgetSprint'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-81', pluginId: 3).save();    //   url: '/budgReport/showBudgetSprint'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-82', pluginId: 3).save();    //   url: '/budgReport/searchBudgetSprint'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-82', pluginId: 3).save();    //   url: '/budgReport/searchBudgetSprint'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'BUDG-82', pluginId: 3).save();    //   url: '/budgReport/searchBudgetSprint'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-94', pluginId: 3).save();    //   url: '/budgReport/downloadForecastingReport'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-94', pluginId: 3).save();    //   url: '/budgReport/downloadForecastingReport'

        // project budget
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-91', pluginId: 3).save();    //   url: '/budgReport/showProjectBudget'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-91', pluginId: 3).save();    //   url: '/budgReport/showProjectBudget'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-92', pluginId: 3).save();    //   url: '/budgReport/listProjectBudget'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-92', pluginId: 3).save();    //   url: '/budgReport/listProjectBudget'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'BUDG-93', pluginId: 3).save();    //   url: '/budgReport/downloadProjectBudget'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'BUDG-93', pluginId: 3).save();    //   url: '/budgReport/downloadProjectBudget'
        return true
    }

    public boolean createRoleFeatureMapForProcurementPlugin() {
        // default
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'PROC-78', pluginId: 5).save();         // url: '/procurement/renderProcurementMenu'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-78', pluginId: 5).save();         // url: '/procurement/renderProcurementMenu'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-78', pluginId: 5).save();         // url: '/procurement/renderProcurementMenu'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-78', pluginId: 5).save();         // url: '/procurement/renderProcurementMenu'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'PROC-78', pluginId: 5).save();         // url: '/procurement/renderProcurementMenu'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'PROC-78', pluginId: 5).save();         // url: '/procurement/renderProcurementMenu'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-78', pluginId: 5).save();        // url: '/procurement/renderProcurementMenu'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'PROC-78', pluginId: 5).save();        // url: '/procurement/renderProcurementMenu'

        // Purchase Request

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-1', pluginId: 5).save();         // url: '/procPurchaseRequest/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-1', pluginId: 5).save();         // url: '/procPurchaseRequest/show'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-1', pluginId: 5).save();         // url: '/procPurchaseRequest/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-1', pluginId: 5).save();        // url: '/procPurchaseRequest/show'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-96', pluginId: 5).save();         // url: '/procPurchaseRequest/unApprovePR'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-96', pluginId: 5).save();         // url: '/procPurchaseRequest/unApprovePR'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-96', pluginId: 5).save();        // url: '/procPurchaseRequest/unApprovePR'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-2', pluginId: 5).save();         // url: '/procPurchaseRequest/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-2', pluginId: 5).save();         // url: '/procPurchaseRequest/create'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-2', pluginId: 5).save();         // url: '/procPurchaseRequest/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-2', pluginId: 5).save();        // url: '/procPurchaseRequest/create'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-3', pluginId: 5).save();         // url:  '/procPurchaseRequest/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-3', pluginId: 5).save();         // url:  '/procPurchaseRequest/select'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-3', pluginId: 5).save();         // url:  '/procPurchaseRequest/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-3', pluginId: 5).save();        // url:  '/procPurchaseRequest/select'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-4', pluginId: 5).save();         // url:  '/procPurchaseRequest/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-4', pluginId: 5).save();         // url:  '/procPurchaseRequest/update'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-4', pluginId: 5).save();         // url:  '/procPurchaseRequest/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-4', pluginId: 5).save();        // url:  '/procPurchaseRequest/update'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-5', pluginId: 5).save();         // url:  '/procPurchaseRequest/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-5', pluginId: 5).save();         // url:  '/procPurchaseRequest/delete'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-5', pluginId: 5).save();         // url:  '/procPurchaseRequest/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-5', pluginId: 5).save();        // url:  '/procPurchaseRequest/delete'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-6', pluginId: 5).save();         // url:  '/procPurchaseRequest/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-6', pluginId: 5).save();         // url:  '/procPurchaseRequest/list'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-6', pluginId: 5).save();         // url:  '/procPurchaseRequest/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-6', pluginId: 5).save();        // url:  '/procPurchaseRequest/list'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-7', pluginId: 5).save();         // url:  '/procPurchaseRequest/approve'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-7', pluginId: 5).save();         // url:  '/procPurchaseRequest/approve'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-7', pluginId: 5).save();         // url:  '/procPurchaseRequest/approve'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-7', pluginId: 5).save();        // url:  '/procPurchaseRequest/approve'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-93', pluginId: 5).save();         // url:  '/procPurchaseRequest/sentMailForPRApproval'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-93', pluginId: 5).save();         // url:  '/procPurchaseRequest/sentMailForPRApproval'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-93', pluginId: 5).save();         // url:  '/procPurchaseRequest/sentMailForPRApproval'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-93', pluginId: 5).save();        // url:  '/procPurchaseRequest/sentMailForPRApproval'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-94', pluginId: 5).save();         // url:  '/procPurchaseOrder/cancelPO'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-94', pluginId: 5).save();         // url:  '/procPurchaseOrder/cancelPO'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-94', pluginId: 5).save();        // url:  '/procPurchaseOrder/cancelPO'


        // Get budget info  for Purchase Request

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-69', pluginId: 5).save();         // url:  '/procPurchaseRequest/getBudgetForPR'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-69', pluginId: 5).save();         // url:  '/procPurchaseRequest/getBudgetForPR'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-69', pluginId: 5).save();         // url:  '/procPurchaseRequest/getBudgetForPR'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-69', pluginId: 5).save();        // url:  '/procPurchaseRequest/getBudgetForPR'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-83', pluginId: 5).save();         // url:  '/procPurchaseRequest/listUnApprovedPR'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-83', pluginId: 5).save();         // url:  '/procPurchaseRequest/listUnApprovedPR'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-83', pluginId: 5).save();        // url:  '/procPurchaseRequest/listUnApprovedPR'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-84', pluginId: 5).save();         // url:  '/procPurchaseRequest/approvePRDashBoard'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-84', pluginId: 5).save();         // url:  '/procPurchaseRequest/approvePRDashBoard'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-84', pluginId: 5).save();        // url:  '/procPurchaseRequest/approvePRDashBoard'

        // Purchase Request Details

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-8', pluginId: 5).save();         // url:  '/procPurchaseRequestDetails/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-8', pluginId: 5).save();         // url:  '/procPurchaseRequestDetails/show'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-8', pluginId: 5).save();         // url:  '/procPurchaseRequestDetails/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-8', pluginId: 5).save();        // url:  '/procPurchaseRequestDetails/show'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-9', pluginId: 5).save();         // url:  '/procPurchaseRequestDetails/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-9', pluginId: 5).save();         // url:  '/procPurchaseRequestDetails/create'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-9', pluginId: 5).save();         // url:  '/procPurchaseRequestDetails/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-9', pluginId: 5).save();        // url:  '/procPurchaseRequestDetails/create'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-10', pluginId: 5).save();         // url:  '/procPurchaseRequestDetails/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-10', pluginId: 5).save();         // url:  '/procPurchaseRequestDetails/select'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-10', pluginId: 5).save();         // url:  '/procPurchaseRequestDetails/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-10', pluginId: 5).save();        // url:  '/procPurchaseRequestDetails/select'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-11', pluginId: 5).save();         // url:  '/procPurchaseRequestDetails/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-11', pluginId: 5).save();         // url:  '/procPurchaseRequestDetails/update'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-11', pluginId: 5).save();         // url:  '/procPurchaseRequestDetails/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-11', pluginId: 5).save();        // url:  '/procPurchaseRequestDetails/update'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-12', pluginId: 5).save();         // url:  '/procPurchaseRequestDetails/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-12', pluginId: 5).save();         // url:  '/procPurchaseRequestDetails/delete'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-12', pluginId: 5).save();         // url:  '/procPurchaseRequestDetails/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-12', pluginId: 5).save();        // url:  '/procPurchaseRequestDetails/delete'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-13', pluginId: 5).save();         // url:  '/procPurchaseRequestDetails/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-13', pluginId: 5).save();         // url:  '/procPurchaseRequestDetails/list'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-13', pluginId: 5).save();         // url:  '/procPurchaseRequestDetails/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-13', pluginId: 5).save();        // url:  '/procPurchaseRequestDetails/list'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-14', pluginId: 5).save();         // url:  '/procPurchaseRequestDetails/getItemList'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-14', pluginId: 5).save();         // url:  '/procPurchaseRequestDetails/getItemList'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-14', pluginId: 5).save();         // url:  '/procPurchaseRequestDetails/getItemList'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-14', pluginId: 5).save();        // url:  '/procPurchaseRequestDetails/getItemList'

        // Purchase Order

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-80', pluginId: 5).save();         // url:  '/procPurchaseOrder/approvePODashBoard'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-80', pluginId: 5).save();         // url:  '/procPurchaseOrder/approvePODashBoard'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-80', pluginId: 5).save();         // url:  '/procPurchaseOrder/approvePODashBoard'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-15', pluginId: 5).save();         // url:  '/procPurchaseOrder/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-15', pluginId: 5).save();         // url:  '/procPurchaseOrder/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-15', pluginId: 5).save();        // url:  '/procPurchaseOrder/show'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-16', pluginId: 5).save();         // url:  '/procPurchaseOrder/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-16', pluginId: 5).save();         // url:  '/procPurchaseOrder/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-16', pluginId: 5).save();        // url:  '/procPurchaseOrder/create'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-17', pluginId: 5).save();         // url:  '/procPurchaseOrder/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-17', pluginId: 5).save();         // url:  '/procPurchaseOrder/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-17', pluginId: 5).save();        // url:  '/procPurchaseOrder/select'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-18', pluginId: 5).save();         // url:  '/procPurchaseOrder/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-18', pluginId: 5).save();         // url:  '/procPurchaseOrder/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-18', pluginId: 5).save();        // url:  '/procPurchaseOrder/update'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-19', pluginId: 5).save();         // url:  '/procPurchaseOrder/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-19', pluginId: 5).save();         // url:  '/procPurchaseOrder/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-19', pluginId: 5).save();        // url:  '/procPurchaseOrder/delete'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-20', pluginId: 5).save();         // url:  '/procPurchaseOrder/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-20', pluginId: 5).save();         // url:  '/procPurchaseOrder/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-20', pluginId: 5).save();        // url:  '/procPurchaseOrder/list'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-22', pluginId: 5).save();         // url:  '/procPurchaseOrder/approve'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-22', pluginId: 5).save();         // url:  '/procPurchaseOrder/approve'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-79', pluginId: 5).save();         // url:  '/procPurchaseOrder/listUnApprovedPO'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-79', pluginId: 5).save();         // url:  '/procPurchaseOrder/listUnApprovedPO'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-79', pluginId: 5).save();        // url:  '/procPurchaseOrder/listUnApprovedPO'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-90', pluginId: 5).save();         // url:  '/procPurchaseOrder/getPOStatusForDashBoard'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-90', pluginId: 5).save();         // url:  '/procPurchaseOrder/getPOStatusForDashBoard'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-90', pluginId: 5).save();        // url:  '/procPurchaseOrder/getPOStatusForDashBoard'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-92', pluginId: 5).save();         // url:  '/procPurchaseOrder/sendForPOApproval'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-92', pluginId: 5).save();         // url:  '/procPurchaseOrder/sendForPOApproval'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-92', pluginId: 5).save();        // url:  '/procPurchaseOrder/sendForPOApproval'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-95', pluginId: 5).save();         // url:  '/procPurchaseOrder/unApprovePO'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-95', pluginId: 5).save();         // url:  '/procPurchaseOrder/unApprovePO'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-95', pluginId: 5).save();        // url:  '/procPurchaseOrder/unApprovePO'

        // cancelled PO
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-97', pluginId: 5).save();         // url:  '/procCancelledPO/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-97', pluginId: 5).save();         // url:  '/procCancelledPO/show'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-98', pluginId: 5).save();         // url:  '/procCancelledPO/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-98', pluginId: 5).save();         // url:  '/procCancelledPO/list'

        // Purchase Order Details
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-23', pluginId: 5).save();         // url:  '/procPurchaseOrderDetails/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-23', pluginId: 5).save();         // url:  '/procPurchaseOrderDetails/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-23', pluginId: 5).save();        // url:  '/procPurchaseOrderDetails/show'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-24', pluginId: 5).save();         // url:  '/procPurchaseOrderDetails/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-24', pluginId: 5).save();         // url:  '/procPurchaseOrderDetails/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-24', pluginId: 5).save();        // url:  '/procPurchaseOrderDetails/create'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-25', pluginId: 5).save();         // url:  '/procPurchaseOrderDetails/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-25', pluginId: 5).save();         // url:  '/procPurchaseOrderDetails/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-25', pluginId: 5).save();        // url:  '/procPurchaseOrderDetails/select'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-26', pluginId: 5).save();         // url:  '/procPurchaseOrderDetails/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-26', pluginId: 5).save();         // url:  '/procPurchaseOrderDetails/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-26', pluginId: 5).save();        // url:  '/procPurchaseOrderDetails/update'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-27', pluginId: 5).save();         // url:  '/procPurchaseOrderDetails/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-27', pluginId: 5).save();         // url:  '/procPurchaseOrderDetails/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-27', pluginId: 5).save();        // url:  '/procPurchaseOrderDetails/delete'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-28', pluginId: 5).save();         // url:  '/procPurchaseOrderDetails/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-28', pluginId: 5).save();         // url:  '/procPurchaseOrderDetails/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-28', pluginId: 5).save();        // url:  '/procPurchaseOrderDetails/list'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-30', pluginId: 5).save();         // url:  '/procPurchaseOrderDetails/getItemListPurchaseOrderDetails'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-30', pluginId: 5).save();         // url:  '/procPurchaseOrderDetails/getItemListPurchaseOrderDetails'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'PROC-30', pluginId: 5).save();         // url:  '/procPurchaseOrderDetails/getItemListPurchaseOrderDetails'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-30', pluginId: 5).save();        // url:  '/procPurchaseOrderDetails/getItemListPurchaseOrderDetails'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'PROC-30', pluginId: 5).save();        // url:  '/procPurchaseOrderDetails/getItemListPurchaseOrderDetails'

        // Proc Indent

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'PROC-31', pluginId: 5).save();         // url:  '/procIndent/show'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-31', pluginId: 5).save();         // url:  '/procIndent/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-31', pluginId: 5).save();         // url:  '/procIndent/show'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-31', pluginId: 5).save();         // url:  '/procIndent/show'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'PROC-31', pluginId: 5).save();         // url:  '/procIndent/show'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'PROC-31', pluginId: 5).save();         // url:  '/procIndent/show'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'PROC-31', pluginId: 5).save();          // url:  '/procIndent/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-31', pluginId: 5).save();        // url:  '/procIndent/show'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'PROC-31', pluginId: 5).save();        // url:  '/procIndent/show'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'PROC-32', pluginId: 5).save();         // url:  '/procIndent/create'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-32', pluginId: 5).save();         // url:  '/procIndent/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-32', pluginId: 5).save();         // url:  '/procIndent/create'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-32', pluginId: 5).save();         // url:  '/procIndent/create'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'PROC-32', pluginId: 5).save();         // url:  '/procIndent/create'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'PROC-32', pluginId: 5).save();         // url:  '/procIndent/create'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'PROC-32', pluginId: 5).save();          // url:  '/procIndent/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-32', pluginId: 5).save();        // url:  '/procIndent/create'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'PROC-32', pluginId: 5).save();        // url:  '/procIndent/create'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'PROC-33', pluginId: 5).save();         // url:  '/procIndent/select'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-33', pluginId: 5).save();         // url:  '/procIndent/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-33', pluginId: 5).save();         // url:  '/procIndent/select'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-33', pluginId: 5).save();         // url:  '/procIndent/select'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'PROC-33', pluginId: 5).save();         // url:  '/procIndent/select'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'PROC-33', pluginId: 5).save();         // url:  '/procIndent/select'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'PROC-33', pluginId: 5).save();          // url:  '/procIndent/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-33', pluginId: 5).save();        // url:  '/procIndent/select'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'PROC-33', pluginId: 5).save();        // url:  '/procIndent/select'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'PROC-34', pluginId: 5).save();         // url:  '/procIndent/update'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-34', pluginId: 5).save();         // url:  '/procIndent/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-34', pluginId: 5).save();         // url:  '/procIndent/update'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-34', pluginId: 5).save();         // url:  '/procIndent/update'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'PROC-34', pluginId: 5).save();         // url:  '/procIndent/update'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'PROC-34', pluginId: 5).save();         // url:  '/procIndent/update'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'PROC-34', pluginId: 5).save();          // url:  '/procIndent/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-34', pluginId: 5).save();        // url:  '/procIndent/update'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'PROC-34', pluginId: 5).save();        // url:  '/procIndent/update'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'PROC-35', pluginId: 5).save();         // url:  '/procIndent/delete'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-35', pluginId: 5).save();         // url:  '/procIndent/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-35', pluginId: 5).save();         // url:  '/procIndent/delete'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-35', pluginId: 5).save();         // url:  '/procIndent/delete'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'PROC-35', pluginId: 5).save();         // url:  '/procIndent/delete'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'PROC-35', pluginId: 5).save();         // url:  '/procIndent/delete'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'PROC-35', pluginId: 5).save();          // url:  '/procIndent/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-35', pluginId: 5).save();        // url:  '/procIndent/delete'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'PROC-35', pluginId: 5).save();        // url:  '/procIndent/delete'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'PROC-36', pluginId: 5).save();         // url:  '/procIndent/list'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-36', pluginId: 5).save();         // url:  '/procIndent/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-36', pluginId: 5).save();         // url:  '/procIndent/list'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-36', pluginId: 5).save();         // url:  '/procIndent/list'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'PROC-36', pluginId: 5).save();         // url:  '/procIndent/list'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'PROC-36', pluginId: 5).save();         // url:  '/procIndent/list'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'PROC-36', pluginId: 5).save();          // url:  '/procIndent/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-36', pluginId: 5).save();        // url:  '/procIndent/list'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'PROC-36', pluginId: 5).save();        // url:  '/procIndent/list'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-81', pluginId: 5).save();         // url:  '/procIndent/approve'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-81', pluginId: 5).save();         // url:  '/procIndent/approve'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-85', pluginId: 5).save();         // url:  '/procIndent/listOfUnApprovedIndent'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-85', pluginId: 5).save();         // url:  '/procIndent/listOfUnApprovedIndent'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-86', pluginId: 5).save();         // url:  '/procIndent/approveIndentDashBoard'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-86', pluginId: 5).save();         // url:  '/procIndent/approveIndentDashBoard'

        // Indent Details

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'PROC-37', pluginId: 5).save();         // url:  '/procIndentDetails/show'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-37', pluginId: 5).save();         // url:  '/procIndentDetails/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-37', pluginId: 5).save();         // url:  '/procIndentDetails/show'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-37', pluginId: 5).save();         // url:  '/procIndentDetails/show'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'PROC-37', pluginId: 5).save();         // url:  '/procIndentDetails/show'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'PROC-37', pluginId: 5).save();         // url:  '/procIndentDetails/show'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'PROC-37', pluginId: 5).save();          // url:  '/procIndentDetails/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-37', pluginId: 5).save();        // url:  '/procIndentDetails/show'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'PROC-37', pluginId: 5).save();        // url:  '/procIndentDetails/show'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'PROC-38', pluginId: 5).save();         // url:  '/procIndentDetails/create'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-38', pluginId: 5).save();         // url:  '/procIndentDetails/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-38', pluginId: 5).save();         // url:  '/procIndentDetails/create'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-38', pluginId: 5).save();         // url:  '/procIndentDetails/create'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'PROC-38', pluginId: 5).save();         // url:  '/procIndentDetails/create'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'PROC-38', pluginId: 5).save();         // url:  '/procIndentDetails/create'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'PROC-38', pluginId: 5).save();          // url:  '/procIndentDetails/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-38', pluginId: 5).save();        // url:  '/procIndentDetails/create'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'PROC-38', pluginId: 5).save();        // url:  '/procIndentDetails/create'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'PROC-39', pluginId: 5).save();         // url:  '/procIndentDetails/select'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-39', pluginId: 5).save();         // url:  '/procIndentDetails/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-39', pluginId: 5).save();         // url:  '/procIndentDetails/select'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-39', pluginId: 5).save();         // url:  '/procIndentDetails/select'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'PROC-39', pluginId: 5).save();         // url:  '/procIndentDetails/select'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'PROC-39', pluginId: 5).save();         // url:  '/procIndentDetails/select'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'PROC-39', pluginId: 5).save();          // url:  '/procIndentDetails/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-39', pluginId: 5).save();        // url:  '/procIndentDetails/select'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'PROC-39', pluginId: 5).save();        // url:  '/procIndentDetails/select'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'PROC-40', pluginId: 5).save();         // url:  '/procIndentDetails/update'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-40', pluginId: 5).save();         // url:  '/procIndentDetails/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-40', pluginId: 5).save();         // url:  '/procIndentDetails/update'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-40', pluginId: 5).save();         // url:  '/procIndentDetails/update'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'PROC-40', pluginId: 5).save();         // url:  '/procIndentDetails/update'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'PROC-40', pluginId: 5).save();         // url:  '/procIndentDetails/update'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'PROC-40', pluginId: 5).save();          // url:  '/procIndentDetails/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-40', pluginId: 5).save();        // url:  '/procIndentDetails/update'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'PROC-40', pluginId: 5).save();        // url:  '/procIndentDetails/update'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'PROC-41', pluginId: 5).save();         // url:  '/procIndentDetails/delete'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-41', pluginId: 5).save();         // url:  '/procIndentDetails/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-41', pluginId: 5).save();         // url:  '/procIndentDetails/delete'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-41', pluginId: 5).save();         // url:  '/procIndentDetails/delete'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'PROC-41', pluginId: 5).save();         // url:  '/procIndentDetails/delete'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'PROC-41', pluginId: 5).save();         // url:  '/procIndentDetails/delete'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'PROC-41', pluginId: 5).save();          // url:  '/procIndentDetails/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-41', pluginId: 5).save();        // url:  '/procIndentDetails/delete'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'PROC-41', pluginId: 5).save();        // url:  '/procIndentDetails/delete'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'PROC-42', pluginId: 5).save();         // url:  '/procIndentDetails/list'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-42', pluginId: 5).save();         // url:  '/procIndentDetails/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-42', pluginId: 5).save();         // url:  '/procIndentDetails/list'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-42', pluginId: 5).save();         // url:  '/procIndentDetails/list'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'PROC-42', pluginId: 5).save();         // url:  '/procIndentDetails/list'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'PROC-42', pluginId: 5).save();         // url:  '/procIndentDetails/list'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'PROC-42', pluginId: 5).save();          // url:  '/procIndentDetails/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-42', pluginId: 5).save();        // url:  '/procIndentDetails/list'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'PROC-42', pluginId: 5).save();        // url:  '/procIndentDetails/list'

        // Purchase Request Report

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-44', pluginId: 5).save();         // url:  '/procReport/showPurchaseRequestRpt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-44', pluginId: 5).save();         // url:  '/procReport/showPurchaseRequestRpt'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-44', pluginId: 5).save();         // url:  '/procReport/showPurchaseRequestRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-44', pluginId: 5).save();        // url:  '/procReport/showPurchaseRequestRpt'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-45', pluginId: 5).save();         // url:  '/procReport/searchPurchaseRequestRpt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-45', pluginId: 5).save();         // url:  '/procReport/searchPurchaseRequestRpt'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-45', pluginId: 5).save();         // url:  '/procReport/searchPurchaseRequestRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-45', pluginId: 5).save();        // url:  '/procReport/searchPurchaseRequestRpt'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-46', pluginId: 5).save();         // url:  '/procReport/downloadPurchaseRequestRpt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-46', pluginId: 5).save();         // url:  '/procReport/downloadPurchaseRequestRpt'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-46', pluginId: 5).save();         // url:  '/procReport/downloadPurchaseRequestRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-46', pluginId: 5).save();        // url:  '/procReport/downloadPurchaseRequestRpt'

        // Purchase Order Report

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-47', pluginId: 5).save();         // url:  '/procReport/showPurchaseOrderRpt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-47', pluginId: 5).save();         // url:  '/procReport/showPurchaseOrderRpt'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-47', pluginId: 5).save();         // url:  '/procReport/showPurchaseOrderRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-47', pluginId: 5).save();        // url:  '/procReport/showPurchaseOrderRpt'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-48', pluginId: 5).save();         // url:  '/procReport/searchPurchaseOrderRpt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-48', pluginId: 5).save();         // url:  '/procReport/searchPurchaseOrderRpt'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-48', pluginId: 5).save();         // url:  '/procReport/searchPurchaseOrderRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-48', pluginId: 5).save();        // url:  '/procReport/searchPurchaseOrderRpt'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-49', pluginId: 5).save();         // url:  '/procReport/downloadPurchaseOrderRpt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-49', pluginId: 5).save();         // url:  '/procReport/downloadPurchaseOrderRpt'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-49', pluginId: 5).save();         // url:  '/procReport/downloadPurchaseOrderRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-49', pluginId: 5).save();        // url:  '/procReport/downloadPurchaseOrderRpt'

        // Pdf Report

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'PROC-50', pluginId: 5).save();         // url:  '/procReport/showIndentRpt'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-50', pluginId: 5).save();         // url:  '/procReport/showIndentRpt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-50', pluginId: 5).save();         // url:  '/procReport/showIndentRpt'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-50', pluginId: 5).save();         // url:  '/procReport/showIndentRpt'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'PROC-50', pluginId: 5).save();         // url:  '/procReport/showIndentRpt'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'PROC-50', pluginId: 5).save();         // url:  '/procReport/showIndentRpt'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'PROC-50', pluginId: 5).save();          // url:  '/procReport/showIndentRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-50', pluginId: 5).save();        // url:  '/procReport/showIndentRpt'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'PROC-50', pluginId: 5).save();        // url:  '/procReport/showIndentRpt'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'PROC-51', pluginId: 5).save();         // url:  '/procReport/searchIndentRpt'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-51', pluginId: 5).save();         // url:  '/procReport/searchIndentRpt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-51', pluginId: 5).save();         // url:  '/procReport/searchIndentRpt'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-51', pluginId: 5).save();         // url:  '/procReport/searchIndentRpt'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'PROC-51', pluginId: 5).save();         // url:  '/procReport/searchIndentRpt'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'PROC-51', pluginId: 5).save();         // url:  '/procReport/searchIndentRpt'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'PROC-51', pluginId: 5).save();          // url:  '/procReport/searchIndentRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-51', pluginId: 5).save();        // url:  '/procReport/searchIndentRpt'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'PROC-51', pluginId: 5).save();        // url:  '/procReport/searchIndentRpt'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'PROC-52', pluginId: 5).save();         // url:  '/procReport/downloadIndentRpt'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-52', pluginId: 5).save();         // url:  '/procReport/downloadIndentRpt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-52', pluginId: 5).save();         // url:  '/procReport/downloadIndentRpt'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'PROC-52', pluginId: 5).save();         // url:  '/procReport/downloadIndentRpt'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'PROC-52', pluginId: 5).save();         // url:  '/procReport/downloadIndentRpt'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'PROC-52', pluginId: 5).save();         // url:  '/procReport/downloadIndentRpt'
        new RoleFeatureMapping(roleTypeId: 8, transactionCode: 'PROC-52', pluginId: 5).save();          // url:  '/procReport/downloadIndentRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-52', pluginId: 5).save();        // url:  '/procReport/downloadIndentRpt'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'PROC-52', pluginId: 5).save();        // url:  '/procReport/downloadIndentRpt'

        // Proc Transport Cost

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-53', pluginId: 5).save();         // url:  '/procTransportCost/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-53', pluginId: 5).save();         // url:  '/procTransportCost/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-53', pluginId: 5).save();        // url:  '/procTransportCost/show'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-54', pluginId: 5).save();         // url:  '/procTransportCost/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-54', pluginId: 5).save();         // url:  '/procTransportCost/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-54', pluginId: 5).save();        // url:  '/procTransportCost/create'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-55', pluginId: 5).save();         // url:  '/procTransportCost/edit'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-55', pluginId: 5).save();         // url:  '/procTransportCost/edit'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-55', pluginId: 5).save();        // url:  '/procTransportCost/edit'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-56', pluginId: 5).save();         // url:  '/procTransportCost/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-56', pluginId: 5).save();         // url:  '/procTransportCost/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-56', pluginId: 5).save();        // url:  '/procTransportCost/update'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-57', pluginId: 5).save();         // url:  '/procTransportCost/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-57', pluginId: 5).save();         // url:  '/procTransportCost/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-57', pluginId: 5).save();        // url:  '/procTransportCost/delete'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-58', pluginId: 5).save();         // url:  '/procTransportCost/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-58', pluginId: 5).save();         // url:  '/procTransportCost/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-58', pluginId: 5).save();        // url:  '/procTransportCost/list'


        // Get budget info  for Purchase Request
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-70', pluginId: 5).save();         // url:  '/procTermsAndCondition/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-70', pluginId: 5).save();         // url:  '/procTermsAndCondition/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-70', pluginId: 5).save();        // url:  '/procTermsAndCondition/show'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-71', pluginId: 5).save();         // url:  '/procTermsAndCondition/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-71', pluginId: 5).save();         // url:  '/procTermsAndCondition/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-71', pluginId: 5).save();        // url:  '/procTermsAndCondition/create'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-72', pluginId: 5).save();         // url:  '/procTermsAndCondition/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-72', pluginId: 5).save();         // url:  '/procTermsAndCondition/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-72', pluginId: 5).save();        // url:  '/procTermsAndCondition/select'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-73', pluginId: 5).save();         // url:  '/procTermsAndCondition/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-73', pluginId: 5).save();         // url:  '/procTermsAndCondition/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-73', pluginId: 5).save();        // url:  '/procTermsAndCondition/update'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-74', pluginId: 5).save();         // url:  '/procTermsAndCondition/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-74', pluginId: 5).save();         // url:  '/procTermsAndCondition/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-74', pluginId: 5).save();        // url:  '/procTermsAndCondition/delete'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-75', pluginId: 5).save();         // url:  '/procTermsAndCondition/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-75', pluginId: 5).save();         // url:  '/procTermsAndCondition/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-75', pluginId: 5).save();        // url:  '/procTermsAndCondition/list'

        // Supplier Wise Purchase Order Report

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-76', pluginId: 5).save();         // url:  '/procReport/showSupplierWisePO'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-76', pluginId: 5).save();         // url:  '/procReport/showSupplierWisePO'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-76', pluginId: 5).save();        // url:  '/procReport/showSupplierWisePO'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-77', pluginId: 5).save();         // url:  '/procReport/listSupplierWisePO'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-77', pluginId: 5).save();         // url:  '/procReport/listSupplierWisePO'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-77', pluginId: 5).save();        // url:  '/procReport/listSupplierWisePO'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-82', pluginId: 5).save();         // url:  '/procReport/downloadSupplierWisePO'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-82', pluginId: 5).save();         // url:  '/procReport/downloadSupplierWisePO'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-82', pluginId: 5).save();        // url:  '/procReport/downloadSupplierWisePO'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'PROC-91', pluginId: 5).save();         // url:  '/procReport/downloadSupplierWisePOCsv'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'PROC-91', pluginId: 5).save();         // url:  '/procReport/downloadSupplierWisePOCsv'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'PROC-91', pluginId: 5).save();        // url:  '/procReport/downloadSupplierWisePOCsv'

        return true
    }

    public boolean createRoleFeatureMapForInventoryPlugin() {
        // default
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'INV-150', pluginId: 4).save();         // url: '/inventory/renderInventoryMenu'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-150', pluginId: 4).save();         // url: '/inventory/renderInventoryMenu'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-150', pluginId: 4).save();         // url: '/inventory/renderInventoryMenu'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-150', pluginId: 4).save();         // url: '/inventory/renderInventoryMenu'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'INV-150', pluginId: 4).save();         // url: '/inventory/renderInventoryMenu'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-150', pluginId: 4).save();         // url: '/inventory/renderInventoryMenu'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-150', pluginId: 4).save();        // url: '/inventory/renderInventoryMenu'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-150', pluginId: 4).save();        // url: '/inventory/renderInventoryMenu'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'INV-150', pluginId: 4).save();        // url: '/inventory/renderInventoryMenu'
        // For Dash Board
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-151', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedConsumption'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-186', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-187', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedInventoryOut'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-188', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedInFromInventory'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-151', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedConsumption'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-186', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-187', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedInventoryOut'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-188', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedInFromInventory'

        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-151', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedConsumption'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-186', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-187', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedInventoryOut'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-188', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedInFromInventory'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-151', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedConsumption'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-186', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-187', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedInventoryOut'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-188', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedInFromInventory'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-151', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedConsumption'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-186', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-187', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedInventoryOut'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-188', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedInFromInventory'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-151', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedConsumption'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-186', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-187', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedInventoryOut'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-188', pluginId: 4).save();         // url: '/invInventoryTransaction/listOfUnApprovedInFromInventory'
        // For inv inventory
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'INV-1', pluginId: 4).save();          // url: '/invInventory/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'INV-2', pluginId: 4).save();          // url: '/invInventory/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'INV-3', pluginId: 4).save();          // url: '/invInventory/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'INV-4', pluginId: 4).save();          // url: '/invInventory/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'INV-5', pluginId: 4).save();          // url: '/invInventory/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'INV-6', pluginId: 4).save();          // url: '/invInventory/list'

        //-------------- > For Inventory Transaction < --------------\\

        // Inventory Transaction-in (from supplier)
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-32', pluginId: 4).save();          // url: '/invInventoryTransaction/showInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-33', pluginId: 4).save();          // url: '/invInventoryTransaction/createInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-34', pluginId: 4).save();          // url: '/invInventoryTransaction/selectInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-35', pluginId: 4).save();          // url: '/invInventoryTransaction/updateInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-36', pluginId: 4).save();          // url: '/invInventoryTransaction/deleteInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-37', pluginId: 4).save();          // url: '/invInventoryTransaction/listInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-38', pluginId: 4).save();          // url: '/invInventoryTransaction/listInventoryByType'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-39', pluginId: 4).save();          // url: '/invInventoryTransaction/listPOBySupplier'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-183', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryByTypeAndProject'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-217', pluginId: 4).save();         // url: '/invInventoryTransaction/listFixedAssetByItemAndProject'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-32', pluginId: 4).save();          // url: '/invInventoryTransaction/showInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-33', pluginId: 4).save();          // url: '/invInventoryTransaction/createInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-34', pluginId: 4).save();          // url: '/invInventoryTransaction/selectInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-35', pluginId: 4).save();          // url: '/invInventoryTransaction/updateInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-36', pluginId: 4).save();          // url: '/invInventoryTransaction/deleteInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-37', pluginId: 4).save();          // url: '/invInventoryTransaction/listInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-38', pluginId: 4).save();          // url: '/invInventoryTransaction/listInventoryByType'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-39', pluginId: 4).save();          // url: '/invInventoryTransaction/listPOBySupplier'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-183', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryByTypeAndProject'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-217', pluginId: 4).save();         // url: '/invInventoryTransaction/listFixedAssetByItemAndProject'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-38', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryByType'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-183', pluginId: 4).save();        // url: '/invInventoryTransaction/listInventoryByTypeAndProject'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-217', pluginId: 4).save();        // url: '/invInventoryTransaction/listFixedAssetByItemAndProject'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-32', pluginId: 4).save();         // url: '/invInventoryTransaction/showInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-33', pluginId: 4).save();         // url: '/invInventoryTransaction/createInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-34', pluginId: 4).save();         // url: '/invInventoryTransaction/selectInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-35', pluginId: 4).save();         // url: '/invInventoryTransaction/updateInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-36', pluginId: 4).save();         // url: '/invInventoryTransaction/deleteInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-37', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-38', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryByType'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-39', pluginId: 4).save();         // url: '/invInventoryTransaction/listPOBySupplier'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-183', pluginId: 4).save();        // url: '/invInventoryTransaction/listInventoryByTypeAndProject'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-217', pluginId: 4).save();        // url: '/invInventoryTransaction/listFixedAssetByItemAndProject'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-217', pluginId: 4).save();         // url: '/invInventoryTransaction/listFixedAssetByItemAndProject'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-217', pluginId: 4).save();         // url: '/invInventoryTransaction/listFixedAssetByItemAndProject'

        // Inventory Transaction-in (from Inventory)
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-40', pluginId: 4).save();         // url: '/invInventoryTransaction/showInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-41', pluginId: 4).save();         // url: '/invInventoryTransaction/createInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-42', pluginId: 4).save();         // url: '/invInventoryTransaction/selectInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-43', pluginId: 4).save();         // url: '/invInventoryTransaction/updateInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-44', pluginId: 4).save();         // url: '/invInventoryTransaction/deleteInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-45', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-46', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryOfTransactionOut'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-47', pluginId: 4).save();         // url: '/invInventoryTransaction/listInvTransaction'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-40', pluginId: 4).save();         // url: '/invInventoryTransaction/showInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-41', pluginId: 4).save();         // url: '/invInventoryTransaction/createInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-42', pluginId: 4).save();         // url: '/invInventoryTransaction/selectInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-43', pluginId: 4).save();         // url: '/invInventoryTransaction/updateInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-44', pluginId: 4).save();         // url: '/invInventoryTransaction/deleteInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-45', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-46', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryOfTransactionOut'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-47', pluginId: 4).save();         // url: '/invInventoryTransaction/listInvTransaction'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-40', pluginId: 4).save();         // url: '/invInventoryTransaction/showInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-41', pluginId: 4).save();         // url: '/invInventoryTransaction/createInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-42', pluginId: 4).save();         // url: '/invInventoryTransaction/selectInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-43', pluginId: 4).save();         // url: '/invInventoryTransaction/updateInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-44', pluginId: 4).save();         // url: '/invInventoryTransaction/deleteInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-45', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-46', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryOfTransactionOut'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-47', pluginId: 4).save();         // url: '/invInventoryTransaction/listInvTransaction'

        // Inventory Consumption
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-48', pluginId: 4).save();         // url: '/invInventoryTransaction/showInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-49', pluginId: 4).save();         // url: '/invInventoryTransaction/createInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-50', pluginId: 4).save();         // url: '/invInventoryTransaction/selectInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-51', pluginId: 4).save();         // url: '/invInventoryTransaction/updateInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-52', pluginId: 4).save();         // url: '/invInventoryTransaction/deleteInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-53', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryConsumption'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-48', pluginId: 4).save();         // url: '/invInventoryTransaction/showInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-49', pluginId: 4).save();         // url: '/invInventoryTransaction/createInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-50', pluginId: 4).save();         // url: '/invInventoryTransaction/selectInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-51', pluginId: 4).save();         // url: '/invInventoryTransaction/updateInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-52', pluginId: 4).save();         // url: '/invInventoryTransaction/deleteInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-53', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryConsumption'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-48', pluginId: 4).save();         // url: '/invInventoryTransaction/showInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-49', pluginId: 4).save();         // url: '/invInventoryTransaction/createInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-50', pluginId: 4).save();         // url: '/invInventoryTransaction/selectInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-51', pluginId: 4).save();         // url: '/invInventoryTransaction/updateInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-52', pluginId: 4).save();         // url: '/invInventoryTransaction/deleteInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-53', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryConsumption'

        // Inventory Out
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-54', pluginId: 4).save();         // url: '/invInventoryTransaction/showInventoryOut'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-55', pluginId: 4).save();         // url: '/invInventoryTransaction/createInventoryOut'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-56', pluginId: 4).save();         // url: '/invInventoryTransaction/selectInventoryOut'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-57', pluginId: 4).save();         // url: '/invInventoryTransaction/updateInventoryOut'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-58', pluginId: 4).save();         // url: '/invInventoryTransaction/deleteInventoryOut'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-59', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryOut'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-54', pluginId: 4).save();         // url: '/invInventoryTransaction/showInventoryOut'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-55', pluginId: 4).save();         // url: '/invInventoryTransaction/createInventoryOut'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-56', pluginId: 4).save();         // url: '/invInventoryTransaction/selectInventoryOut'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-57', pluginId: 4).save();         // url: '/invInventoryTransaction/updateInventoryOut'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-58', pluginId: 4).save();         // url: '/invInventoryTransaction/deleteInventoryOut'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-59', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryOut'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-54', pluginId: 4).save();         // url: '/invInventoryTransaction/showInventoryOut'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-55', pluginId: 4).save();         // url: '/invInventoryTransaction/createInventoryOut'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-56', pluginId: 4).save();         // url: '/invInventoryTransaction/selectInventoryOut'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-57', pluginId: 4).save();         // url: '/invInventoryTransaction/updateInventoryOut'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-58', pluginId: 4).save();         // url: '/invInventoryTransaction/deleteInventoryOut'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-59', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryOut'

        // Inventory -Production
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-66', pluginId: 4).save();         // url: '/invInventoryTransaction/showInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-67', pluginId: 4).save();         // url: '/invInventoryTransaction/createInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-68', pluginId: 4).save();         // url: '/invInventoryTransaction/updateInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-69', pluginId: 4).save();         // url: '/invInventoryTransaction/deleteInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-70', pluginId: 4).save();         // url: '/invInventoryTransaction/selectInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-71', pluginId: 4).save();         // url: '/invInventoryTransaction/listInvProductionWithConsumption'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-66', pluginId: 4).save();         // url: '/invInventoryTransaction/showInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-67', pluginId: 4).save();         // url: '/invInventoryTransaction/createInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-68', pluginId: 4).save();         // url: '/invInventoryTransaction/updateInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-69', pluginId: 4).save();         // url: '/invInventoryTransaction/deleteInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-70', pluginId: 4).save();         // url: '/invInventoryTransaction/selectInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-71', pluginId: 4).save();         // url: '/invInventoryTransaction/listInvProductionWithConsumption'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-66', pluginId: 4).save();         // url: '/invInventoryTransaction/showInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-67', pluginId: 4).save();         // url: '/invInventoryTransaction/createInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-68', pluginId: 4).save();         // url: '/invInventoryTransaction/updateInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-69', pluginId: 4).save();         // url: '/invInventoryTransaction/deleteInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-70', pluginId: 4).save();         // url: '/invInventoryTransaction/selectInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-71', pluginId: 4).save();         // url: '/invInventoryTransaction/listInvProductionWithConsumption'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-180', pluginId: 4).save();         // url: '/invInventoryTransaction/showApprovedProdWithConsump'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-181', pluginId: 4).save();         // url: '/invInventoryTransaction/listApprovedProdWithConsump'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-66', pluginId: 4).save();         // url: '/invInventoryTransaction/showInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-67', pluginId: 4).save();         // url: '/invInventoryTransaction/createInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-68', pluginId: 4).save();         // url: '/invInventoryTransaction/updateInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-69', pluginId: 4).save();         // url: '/invInventoryTransaction/deleteInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-70', pluginId: 4).save();         // url: '/invInventoryTransaction/selectInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-71', pluginId: 4).save();         // url: '/invInventoryTransaction/listInvProductionWithConsumption'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-179', pluginId: 4).save();         // url: '/invInventoryTransaction/approveInvProdWithConsumption'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-180', pluginId: 4).save();         // url: '/invInventoryTransaction/showApprovedProdWithConsump'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-181', pluginId: 4).save();         // url: '/invInventoryTransaction/listApprovedProdWithConsump'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-182', pluginId: 4).save();         // url: '/invInventoryTransaction/adjustInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-184', pluginId: 4).save();         // url: '/invInventoryTransaction/reverseAdjust'
        // Production Line Item
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-72', pluginId: 4).save();         // url: '/invProductionLineItem/show'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-73', pluginId: 4).save();         // url: '/invProductionLineItem/create'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-74', pluginId: 4).save();         // url: '/invProductionLineItem/select'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-75', pluginId: 4).save();         // url: '/invProductionLineItem/update'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-76', pluginId: 4).save();         // url: '/invProductionLineItem/delete'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-77', pluginId: 4).save();         // url: '/invProductionLineItem/list'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-72', pluginId: 4).save();         // url: '/invProductionLineItem/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-73', pluginId: 4).save();         // url: '/invProductionLineItem/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-74', pluginId: 4).save();         // url: '/invProductionLineItem/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-75', pluginId: 4).save();         // url: '/invProductionLineItem/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-76', pluginId: 4).save();         // url: '/invProductionLineItem/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-77', pluginId: 4).save();         // url: '/invProductionLineItem/list'

        // For Inventory Transaction Details

        // For Inventory-out-details
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-78', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showUnApprovedInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-79', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/createInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-80', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/selectInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-81', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/updateInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-82', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/deleteInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-83', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listUnApprovedInventoryOutDetails'

        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-172', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showApprovedInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-173', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listApprovedInventoryOutDetails'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-78', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showUnApprovedInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-79', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/createInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-80', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/selectInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-81', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/updateInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-82', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/deleteInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-83', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listUnApprovedInventoryOutDetails'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-78', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showUnApprovedInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-79', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/createInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-80', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/selectInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-81', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/updateInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-82', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/deleteInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-83', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listUnApprovedInventoryOutDetails'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-171', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/approveInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-172', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showApprovedInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-173', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listApprovedInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-174', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/adjustInvOut'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-192', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/reverseAdjustInvOut'

        // Inventory-in-details (from supplier)
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-90', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showUnapprovedInvInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-91', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/createInventoryInDetailsFromSupplier'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-92', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/selectInventoryInDetailsFromSupplier'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-93', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/updateInventoryInDetailsFromSupplier'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-94', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/deleteInventoryInDetailsFromSupplier'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-95', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listUnapprovedInvInFromSupplier'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-90', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showUnapprovedInvInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-91', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/createInventoryInDetailsFromSupplier'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-92', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/selectInventoryInDetailsFromSupplier'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-93', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/updateInventoryInDetailsFromSupplier'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-94', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/deleteInventoryInDetailsFromSupplier'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-95', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listUnapprovedInvInFromSupplier'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-90', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showUnapprovedInvInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-91', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/createInventoryInDetailsFromSupplier'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-92', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/selectInventoryInDetailsFromSupplier'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-93', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/updateInventoryInDetailsFromSupplier'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-94', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/deleteInventoryInDetailsFromSupplier'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-95', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listUnapprovedInvInFromSupplier'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-164', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/approveInventoryInDetailsFromSupplier'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-165', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showApprovedInvInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-166', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listApprovedInvInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-167', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/adjustInvInFromSupplier'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-190', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/reverseAdjustInvInFromSupplier'

        // Inventory-in-details (from Inventory)
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-96', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showUnapprovedInvInFromInventory'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-97', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/createInventoryInDetailsFromInventory'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-98', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/selectInventoryInDetailsFromInventory'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-99', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/updateInventoryInDetailsFromInventory'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-100', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/deleteInventoryInDetailsFromInventory'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-101', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listUnapprovedInvInFromInventory'

        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-169', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showApprovedInvInFromInventory'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-170', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listApprovedInvInFromInventory'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-96', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showUnapprovedInvInFromInventory'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-97', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/createInventoryInDetailsFromInventory'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-98', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/selectInventoryInDetailsFromInventory'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-99', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/updateInventoryInDetailsFromInventory'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-100', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/deleteInventoryInDetailsFromInventory'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-101', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listUnapprovedInvInFromInventory'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-96', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showUnapprovedInvInFromInventory'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-97', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/createInventoryInDetailsFromInventory'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-98', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/selectInventoryInDetailsFromInventory'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-99', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/updateInventoryInDetailsFromInventory'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-100', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/deleteInventoryInDetailsFromInventory'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-101', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listUnapprovedInvInFromInventory'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-168', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/approveInventoryInDetailsFromInventory'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-169', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showApprovedInvInFromInventory'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-170', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listApprovedInvInFromInventory'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-191', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/reverseAdjustInvInFromInventory'

        // Inventory consumption Details
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-102', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showUnApprovedInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-103', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/createInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-104', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/selectInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-105', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/updateInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-106', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/deleteInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-107', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listUnApprovedInventoryConsumptionDetails'

        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-108', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/approveInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-176', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showApprovedInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-177', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listApprovedInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-178', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/adjustInvConsumption'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-185', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/reverseAdjustInvConsumption'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-158', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listFixedAssetByInventoryId'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-159', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listFixedAssetByInventoryIdAndItemId'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-102', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showUnApprovedInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-103', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/createInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-104', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/selectInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-105', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/updateInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-106', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/deleteInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-107', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listUnApprovedInventoryConsumptionDetails'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-176', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showApprovedInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-177', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listApprovedInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-158', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listFixedAssetByInventoryId'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-159', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listFixedAssetByInventoryIdAndItemId'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-102', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showUnApprovedInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-103', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/createInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-104', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/selectInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-105', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/updateInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-106', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/deleteInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-107', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listUnApprovedInventoryConsumptionDetails'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-176', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showApprovedInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-177', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listApprovedInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-158', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listFixedAssetByInventoryId'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-159', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listFixedAssetByInventoryIdAndItemId'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-158', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listFixedAssetByInventoryId'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-159', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listFixedAssetByInventoryIdAndItemId'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-158', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listFixedAssetByInventoryId'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-159', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/listFixedAssetByInventoryIdAndItemId'

        // Get All Supplier List
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-111', pluginId: 4).save();         // url: '/supplier/getAllSupplierList'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-111', pluginId: 4).save();         // url: '/supplier/getAllSupplierList'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-111', pluginId: 4).save();         // url: '/supplier/getAllSupplierList'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'INV-111', pluginId: 4).save();         // url: '/supplier/getAllSupplierList'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-111', pluginId: 4).save();        // url: '/supplier/getAllSupplierList'

        // Production Line Item Details
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-112', pluginId: 4).save();         // url: '/invProductionDetails/show'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-113', pluginId: 4).save();         // url: '/invProductionDetails/create'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-114', pluginId: 4).save();         // url: '/invProductionDetails/select'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-115', pluginId: 4).save();         // url: '/invProductionDetails/update'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-116', pluginId: 4).save();         // url: '/invProductionDetails/delete'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-117', pluginId: 4).save();         // url: '/invProductionDetails/list'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-118', pluginId: 4).save();         // url: '/invProductionDetails/getBothMaterials'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-112', pluginId: 4).save();         // url: '/invProductionDetails/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-113', pluginId: 4).save();         // url: '/invProductionDetails/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-114', pluginId: 4).save();         // url: '/invProductionDetails/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-115', pluginId: 4).save();         // url: '/invProductionDetails/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-116', pluginId: 4).save();         // url: '/invProductionDetails/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-117', pluginId: 4).save();         // url: '/invProductionDetails/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-118', pluginId: 4).save();         // url: '/invProductionDetails/getBothMaterials'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-118', pluginId: 4).save();         // url: '/invProductionDetails/getBothMaterials'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-118', pluginId: 4).save();        // url: '/invProductionDetails/getBothMaterials'

        // Recalculate valuation
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'INV-148', pluginId: 4).save();         // url: '/invInventoryTransaction/reCalculateValuation'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'INV-203', pluginId: 4).save();         // url: '/invInventoryTransaction/showReCalculateValuation'

        // Production Overhead Cost modification
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-210', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showInvModifyOverheadCost'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-211', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/searchInvModifyOverheadCost'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-212', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/updateInvModifyOverheadCost'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-213', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/getInvProdFinishedMaterialByLineItemId'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-210', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showInvModifyOverheadCost'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-211', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/searchInvModifyOverheadCost'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-212', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/updateInvModifyOverheadCost'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-213', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/getInvProdFinishedMaterialByLineItemId'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-210', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/showInvModifyOverheadCost'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-211', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/searchInvModifyOverheadCost'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-212', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/updateInvModifyOverheadCost'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-213', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/getInvProdFinishedMaterialByLineItemId'

        // refresh dropDown using remote url
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-233', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/dropDownInventoryItemConsumptionReload'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-233', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/dropDownInventoryItemConsumptionReload'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-233', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/dropDownInventoryItemConsumptionReload'

        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-234', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/dropDownInventoryItemInFromInventoryReload'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-234', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/dropDownInventoryItemInFromInventoryReload'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-234', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/dropDownInventoryItemInFromInventoryReload'

        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-235', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/dropDownInventoryItemInFromSupplierReload'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-235', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/dropDownInventoryItemInFromSupplierReload'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-235', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/dropDownInventoryItemInFromSupplierReload'

        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-236', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/dropDownInventoryItemOutReload'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-236', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/dropDownInventoryItemOutReload'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-236', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/dropDownInventoryItemOutReload'

        // Inventory Report

        // inventory invoice
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-119', pluginId: 4).save();         // url: '/invReport/showInvoice'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-120', pluginId: 4).save();         // url: '/invReport/searchInvoice'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-121', pluginId: 4).save();         // url: '/invReport/downloadInvoice'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-119', pluginId: 4).save();         // url: '/invReport/showInvoice'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-120', pluginId: 4).save();         // url: '/invReport/searchInvoice'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-121', pluginId: 4).save();         // url: '/invReport/downloadInvoice'

        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-119', pluginId: 4).save();         // url: '/invReport/showInvoice'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-120', pluginId: 4).save();         // url: '/invReport/searchInvoice'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-121', pluginId: 4).save();         // url: '/invReport/downloadInvoice'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-119', pluginId: 4).save();         // url: '/invReport/showInvoice'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-120', pluginId: 4).save();         // url: '/invReport/searchInvoice'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-121', pluginId: 4).save();         // url: '/invReport/downloadInvoice'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-119', pluginId: 4).save();         // url: '/invReport/showInvoice'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-120', pluginId: 4).save();         // url: '/invReport/searchInvoice'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-121', pluginId: 4).save();         // url: '/invReport/downloadInvoice'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-119', pluginId: 4).save();         // url: '/invReport/showInvoice'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-120', pluginId: 4).save();         // url: '/invReport/searchInvoice'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-121', pluginId: 4).save();         // url: '/invReport/downloadInvoice'

        // inventory stock
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-122', pluginId: 4).save();         // url: '/invReport/inventoryStock'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-124', pluginId: 4).save();         // url: '/invReport/listInventoryStock'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-125', pluginId: 4).save();         // url: '/invReport/downloadInventoryStock'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-223', pluginId: 4).save();         // url: '/invReport/downloadInventoryStockCsv'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-122', pluginId: 4).save();         // url: '/invReport/inventoryStock'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-124', pluginId: 4).save();         // url: '/invReport/listInventoryStock'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-125', pluginId: 4).save();         // url: '/invReport/downloadInventoryStock'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-223', pluginId: 4).save();         // url: '/invReport/downloadInventoryStockCsv'

        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-122', pluginId: 4).save();         // url: '/invReport/inventoryStock'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-124', pluginId: 4).save();         // url: '/invReport/listInventoryStock'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-125', pluginId: 4).save();         // url: '/invReport/downloadInventoryStock'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-223', pluginId: 4).save();         // url: '/invReport/downloadInventoryStockCsv'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-122', pluginId: 4).save();         // url: '/invReport/inventoryStock'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-124', pluginId: 4).save();         // url: '/invReport/listInventoryStock'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-125', pluginId: 4).save();         // url: '/invReport/downloadInventoryStock'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-223', pluginId: 4).save();         // url: '/invReport/downloadInventoryStockCsv'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-122', pluginId: 4).save();         // url: '/invReport/inventoryStock'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-124', pluginId: 4).save();         // url: '/invReport/listInventoryStock'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-125', pluginId: 4).save();         // url: '/invReport/downloadInventoryStock'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-223', pluginId: 4).save();         // url: '/invReport/downloadInventoryStockCsv'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-122', pluginId: 4).save();         // url: '/invReport/inventoryStock'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-124', pluginId: 4).save();         // url: '/invReport/listInventoryStock'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-125', pluginId: 4).save();         // url: '/invReport/downloadInventoryStock'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-223', pluginId: 4).save();         // url: '/invReport/downloadInventoryStockCsv'

        // item stock
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-126', pluginId: 4).save();         // url: '/invReport/showItemStock'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-127', pluginId: 4).save();         // url: '/invReport/listItemStock'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-128', pluginId: 4).save();         // url: '/invReport/getStockDetailsListByItemId'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-126', pluginId: 4).save();         // url: '/invReport/showItemStock'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-127', pluginId: 4).save();         // url: '/invReport/listItemStock'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-128', pluginId: 4).save();         // url: '/invReport/getStockDetailsListByItemId'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-126', pluginId: 4).save();         // url: '/invReport/showItemStock'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-127', pluginId: 4).save();         // url: '/invReport/listItemStock'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-128', pluginId: 4).save();         // url: '/invReport/getStockDetailsListByItemId'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-126', pluginId: 4).save();         // url: '/invReport/showItemStock'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-127', pluginId: 4).save();         // url: '/invReport/listItemStock'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-128', pluginId: 4).save();         // url: '/invReport/getStockDetailsListByItemId'

        // Inventory Status
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-160', pluginId: 4).save();         // url: '/invReport/showInventoryStatusWithQuantityAndValue'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-161', pluginId: 4).save();         // url: '/invReport/listInventoryStatusWithQuantityAndValue'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-162', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantityAndValue'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-224', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantityAndValueCsv'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-160', pluginId: 4).save();         // url: '/invReport/showInventoryStatusWithQuantityAndValue'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-161', pluginId: 4).save();         // url: '/invReport/listInventoryStatusWithQuantityAndValue'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-162', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantityAndValue'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-224', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantityAndValueCsv'

        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-160', pluginId: 4).save();         // url: '/invReport/showInventoryStatusWithQuantityAndValue'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-161', pluginId: 4).save();         // url: '/invReport/listInventoryStatusWithQuantityAndValue'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-162', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantityAndValue'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-224', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantityAndValueCsv'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-160', pluginId: 4).save();         // url: '/invReport/showInventoryStatusWithQuantityAndValue'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-161', pluginId: 4).save();         // url: '/invReport/listInventoryStatusWithQuantityAndValue'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-162', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantityAndValue'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-224', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantityAndValueCsv'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-160', pluginId: 4).save();         // url: '/invReport/showInventoryStatusWithQuantityAndValue'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-161', pluginId: 4).save();         // url: '/invReport/listInventoryStatusWithQuantityAndValue'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-162', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantityAndValue'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-224', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantityAndValueCsv'

        // Inventory valuation
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-129', pluginId: 4).save();         // url: '/invReport/showInventoryValuation'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-130', pluginId: 4).save();         // url: '/invReport/searchInventoryValuation'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-131', pluginId: 4).save();         // url: '/invReport/downloadInventoryValuation'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-227', pluginId: 4).save();         // url: '/invReport/downloadInventoryValuationCsv'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-129', pluginId: 4).save();         // url: '/invReport/showInventoryValuation'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-130', pluginId: 4).save();         // url: '/invReport/searchInventoryValuation'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-131', pluginId: 4).save();         // url: '/invReport/downloadInventoryValuation'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-227', pluginId: 4).save();         // url: '/invReport/downloadInventoryValuationCsv'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-129', pluginId: 4).save();         // url: '/invReport/showInventoryValuation'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-130', pluginId: 4).save();         // url: '/invReport/searchInventoryValuation'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-131', pluginId: 4).save();         // url: '/invReport/downloadInventoryValuation'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-227', pluginId: 4).save();         // url: '/invReport/downloadInventoryValuationCsv'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-129', pluginId: 4).save();         // url: '/invReport/showInventoryValuation'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-130', pluginId: 4).save();         // url: '/invReport/searchInventoryValuation'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-131', pluginId: 4).save();         // url: '/invReport/downloadInventoryValuation'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-227', pluginId: 4).save();         // url: '/invReport/downloadInventoryValuationCsv'


        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-132', pluginId: 4).save();         // url: '/invReport/showInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-133', pluginId: 4).save();         // url: '/invReport/searchInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-134', pluginId: 4).save();         // url: '/invReport/downloadInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-226', pluginId: 4).save();         // url: '/invReport/downloadInventoryTransactionListCsv'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-132', pluginId: 4).save();         // url: '/invReport/showInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-133', pluginId: 4).save();         // url: '/invReport/searchInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-134', pluginId: 4).save();         // url: '/invReport/downloadInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-226', pluginId: 4).save();         // url: '/invReport/downloadInventoryTransactionListCsv'

        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-132', pluginId: 4).save();         // url: '/invReport/showInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-133', pluginId: 4).save();         // url: '/invReport/searchInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-134', pluginId: 4).save();         // url: '/invReport/downloadInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-226', pluginId: 4).save();         // url: '/invReport/downloadInventoryTransactionListCsv'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-132', pluginId: 4).save();         // url: '/invReport/showInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-133', pluginId: 4).save();         // url: '/invReport/searchInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-134', pluginId: 4).save();         // url: '/invReport/downloadInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-226', pluginId: 4).save();         // url: '/invReport/downloadInventoryTransactionListCsv'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-132', pluginId: 4).save();         // url: '/invReport/showInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-133', pluginId: 4).save();         // url: '/invReport/searchInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-134', pluginId: 4).save();         // url: '/invReport/downloadInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-226', pluginId: 4).save();         // url: '/invReport/downloadInventoryTransactionListCsv'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-132', pluginId: 4).save();         // url: '/invReport/showInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-133', pluginId: 4).save();         // url: '/invReport/searchInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-134', pluginId: 4).save();         // url: '/invReport/downloadInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-226', pluginId: 4).save();         // url: '/invReport/downloadInventoryTransactionListCsv'

        // Inventory summary
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-138', pluginId: 4).save();         // url: '/invReport/showInventorySummary'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-139', pluginId: 4).save();         // url: '/invReport/getInventorySummary'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-140', pluginId: 4).save();         // url: '/invReport/downloadInventorySummary'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-222', pluginId: 4).save();         // url: '/invReport/downloadInventorySummaryCsv'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-138', pluginId: 4).save();         // url: '/invReport/showInventorySummary'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-139', pluginId: 4).save();         // url: '/invReport/getInventorySummary'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-140', pluginId: 4).save();         // url: '/invReport/downloadInventorySummary'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-222', pluginId: 4).save();         // url: '/invReport/downloadInventorySummaryCsv'

        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-138', pluginId: 4).save();         // url: '/invReport/showInventorySummary'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-139', pluginId: 4).save();         // url: '/invReport/getInventorySummary'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-140', pluginId: 4).save();         // url: '/invReport/downloadInventorySummary'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-222', pluginId: 4).save();         // url: '/invReport/downloadInventorySummaryCsv'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-138', pluginId: 4).save();         // url: '/invReport/showInventorySummary'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-139', pluginId: 4).save();         // url: '/invReport/getInventorySummary'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-140', pluginId: 4).save();         // url: '/invReport/downloadInventorySummary'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-222', pluginId: 4).save();         // url: '/invReport/downloadInventorySummaryCsv'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-138', pluginId: 4).save();         // url: '/invReport/showInventorySummary'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-139', pluginId: 4).save();         // url: '/invReport/getInventorySummary'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-140', pluginId: 4).save();         // url: '/invReport/downloadInventorySummary'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-222', pluginId: 4).save();         // url: '/invReport/downloadInventorySummaryCsv'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-138', pluginId: 4).save();         // url: '/invReport/showInventorySummary'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-139', pluginId: 4).save();         // url: '/invReport/getInventorySummary'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-140', pluginId: 4).save();         // url: '/invReport/downloadInventorySummary'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-222', pluginId: 4).save();         // url: '/invReport/downloadInventorySummaryCsv'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-141', pluginId: 4).save();         // url: '/invReport/showConsumedItemList'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-142', pluginId: 4).save();         // url: '/invReport/listBudgetOfConsumption'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-143', pluginId: 4).save();         // url: '/invReport/getConsumedItemList'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-220', pluginId: 4).save();         // url: '/invReport/downloadForConsumedItemList'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-141', pluginId: 4).save();         // url: '/invReport/showConsumedItemList'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-142', pluginId: 4).save();         // url: '/invReport/listBudgetOfConsumption'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-143', pluginId: 4).save();         // url: '/invReport/getConsumedItemList'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-220', pluginId: 4).save();         // url: '/invReport/downloadForConsumedItemList'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-141', pluginId: 4).save();         // url: '/invReport/showConsumedItemList'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-142', pluginId: 4).save();         // url: '/invReport/listBudgetOfConsumption'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-143', pluginId: 4).save();         // url: '/invReport/getConsumedItemList'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-220', pluginId: 4).save();         // url: '/invReport/downloadForConsumedItemList'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'INV-144', pluginId: 4).save();         // url: '/invInventoryTransaction/listAllInventoryByType'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'INV-149', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryIsFactoryByType'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-144', pluginId: 4).save();         // url: '/invInventoryTransaction/listAllInventoryByType'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-149', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryIsFactoryByType'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-144', pluginId: 4).save();         // url: '/invInventoryTransaction/listAllInventoryByType'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-149', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryIsFactoryByType'

        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-144', pluginId: 4).save();         // url: '/invInventoryTransaction/listAllInventoryByType'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-149', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryIsFactoryByType'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-144', pluginId: 4).save();         // url: '/invInventoryTransaction/listAllInventoryByType'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-149', pluginId: 4).save();         // url: '/invInventoryTransaction/listInventoryIsFactoryByType'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-144', pluginId: 4).save();        // url: '/invInventoryTransaction/listAllInventoryByType'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-149', pluginId: 4).save();        // url: '/invInventoryTransaction/listInventoryIsFactoryByType'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-152', pluginId: 4).save();         // url: '/invReport/showItemReceivedStock'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-153', pluginId: 4).save();         // url: '/invReport/listItemReceivedStock'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-154', pluginId: 4).save();         // url: '/invReport/downloadItemReceivedStock'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-232', pluginId: 4).save();         // url: '/invReport/downloadItemReceivedGroupBySupplier'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-228', pluginId: 4).save();         // url: '/invReport/downloadItemReceivedStockCsv'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-152', pluginId: 4).save();         // url: '/invReport/showItemReceivedStock'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-153', pluginId: 4).save();         // url: '/invReport/listItemReceivedStock'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-154', pluginId: 4).save();         // url: '/invReport/downloadItemReceivedStock'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-232', pluginId: 4).save();         // url: '/invReport/downloadItemReceivedGroupBySupplier'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-228', pluginId: 4).save();         // url: '/invReport/downloadItemReceivedStockCsv'

        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-152', pluginId: 4).save();         // url: '/invReport/showItemReceivedStock'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-153', pluginId: 4).save();         // url: '/invReport/listItemReceivedStock'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-154', pluginId: 4).save();         // url: '/invReport/downloadItemReceivedStock'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-232', pluginId: 4).save();         // url: '/invReport/downloadItemReceivedGroupBySupplier'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-228', pluginId: 4).save();         // url: '/invReport/downloadItemReceivedStockCsv'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-152', pluginId: 4).save();         // url: '/invReport/showItemReceivedStock'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-153', pluginId: 4).save();         // url: '/invReport/listItemReceivedStock'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-154', pluginId: 4).save();         // url: '/invReport/downloadItemReceivedStock'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-232', pluginId: 4).save();         // url: '/invReport/downloadItemReceivedGroupBySupplier'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-228', pluginId: 4).save();         // url: '/invReport/downloadItemReceivedStockCsv'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-155', pluginId: 4).save();         // url: '/invReport/showItemWiseBudgetSummary'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-156', pluginId: 4).save();         // url: '/invReport/listItemWiseBudgetSummary'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-157', pluginId: 4).save();         // url: '/invReport/downloadItemWiseBudgetSummary'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-229', pluginId: 4).save();         // url: '/invReport/downloadItemWiseBudgetSummaryCsv'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-155', pluginId: 4).save();         // url: '/invReport/showItemWiseBudgetSummary'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-156', pluginId: 4).save();         // url: '/invReport/listItemWiseBudgetSummary'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-157', pluginId: 4).save();         // url: '/invReport/downloadItemWiseBudgetSummary'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-229', pluginId: 4).save();         // url: '/invReport/downloadItemWiseBudgetSummaryCsv'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-155', pluginId: 4).save();         // url: '/invReport/showItemWiseBudgetSummary'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-156', pluginId: 4).save();         // url: '/invReport/listItemWiseBudgetSummary'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-157', pluginId: 4).save();         // url: '/invReport/downloadItemWiseBudgetSummary'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-229', pluginId: 4).save();         // url: '/invReport/downloadItemWiseBudgetSummaryCsv'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-155', pluginId: 4).save();         // url: '/invReport/showItemWiseBudgetSummary'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-156', pluginId: 4).save();         // url: '/invReport/listItemWiseBudgetSummary'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-157', pluginId: 4).save();         // url: '/invReport/downloadItemWiseBudgetSummary'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-229', pluginId: 4).save();         // url: '/invReport/downloadItemWiseBudgetSummaryCsv'

        // production Report
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-193', pluginId: 4).save();         // url: '/invReport/showInventoryProductionRpt'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-194', pluginId: 4).save();         // url: '/invReport/searchInventoryProductionRpt'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-195', pluginId: 4).save();         // url: '/invReport/downloadInventoryProductionRpt'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-193', pluginId: 4).save();         // url: '/invReport/showInventoryProductionRpt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-194', pluginId: 4).save();         // url: '/invReport/searchInventoryProductionRpt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-195', pluginId: 4).save();         // url: '/invReport/downloadInventoryProductionRpt'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-193', pluginId: 4).save();         // url: '/invReport/showInventoryProductionRpt'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-194', pluginId: 4).save();         // url: '/invReport/searchInventoryProductionRpt'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-195', pluginId: 4).save();         // url: '/invReport/downloadInventoryProductionRpt'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-193', pluginId: 4).save();         // url: '/invReport/showInventoryProductionRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-194', pluginId: 4).save();         // url: '/invReport/searchInventoryProductionRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-195', pluginId: 4).save();         // url: '/invReport/downloadInventoryProductionRpt'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-193', pluginId: 4).save();         // url: '/invReport/showInventoryProductionRpt'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-194', pluginId: 4).save();         // url: '/invReport/searchInventoryProductionRpt'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-195', pluginId: 4).save();         // url: '/invReport/downloadInventoryProductionRpt'

        // Supplier Challlan Report
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-196', pluginId: 4).save();         // url: '/invReport/showSupplierChalan'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-197', pluginId: 4).save();         // url: '/invReport/listSupplierChalan'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-196', pluginId: 4).save();         // url: '/invReport/showSupplierChalan'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-197', pluginId: 4).save();         // url: '/invReport/listSupplierChalan'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-196', pluginId: 4).save();         // url: '/invReport/showSupplierChalan'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-197', pluginId: 4).save();         // url: '/invReport/listSupplierChalan'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-196', pluginId: 4).save();         // url: '/invReport/showSupplierChalan'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-197', pluginId: 4).save();         // url: '/invReport/listSupplierChalan'

        // PO Item Received Report

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-198', pluginId: 4).save();         // url: '/invReport/showPoItemReceived'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-199', pluginId: 4).save();         // url: '/invReport/listPoItemReceived'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-200', pluginId: 4).save();         // url: '/invReport/downloadPoItemReceived'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-230', pluginId: 4).save();         // url: '/invReport/downloadPoItemReceivedCsv'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-198', pluginId: 4).save();         // url: '/invReport/showPoItemReceived'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-199', pluginId: 4).save();         // url: '/invReport/listPoItemReceived'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-200', pluginId: 4).save();         // url: '/invReport/downloadPoItemReceived'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-230', pluginId: 4).save();         // url: '/invReport/downloadPoItemReceivedCsv'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-198', pluginId: 4).save();         // url: '/invReport/showPoItemReceived'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-199', pluginId: 4).save();         // url: '/invReport/listPoItemReceived'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-200', pluginId: 4).save();         // url: '/invReport/downloadPoItemReceived'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-230', pluginId: 4).save();         // url: '/invReport/downloadPoItemReceivedCsv'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-198', pluginId: 4).save();         // url: '/invReport/showPoItemReceived'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-199', pluginId: 4).save();         // url: '/invReport/listPoItemReceived'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-200', pluginId: 4).save();         // url: '/invReport/downloadPoItemReceived'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-230', pluginId: 4).save();         // url: '/invReport/downloadPoItemReceivedCsv'

        // Acknowledge & download supplier chalan report

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-201', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/acknowledgeInvoiceFromSupplier'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-202', pluginId: 4).save();         // url: '/invReport/downloadSupplierChalanReport'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-221', pluginId: 4).save();         // url: '/invReport/downloadSupplierChalanCsvReport'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-201', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/acknowledgeInvoiceFromSupplier'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-202', pluginId: 4).save();         // url: '/invReport/downloadSupplierChalanReport'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-221', pluginId: 4).save();         // url: '/invReport/downloadSupplierChalanCsvReport'

        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'INV-201', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/acknowledgeInvoiceFromSupplier'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'INV-202', pluginId: 4).save();         // url: '/invReport/downloadSupplierChalanReport'
        new RoleFeatureMapping(roleTypeId: -7, transactionCode: 'INV-221', pluginId: 4).save();         // url: '/invReport/downloadSupplierChalanCsvReport'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-201', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/acknowledgeInvoiceFromSupplier'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-202', pluginId: 4).save();         // url: '/invReport/downloadSupplierChalanReport'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-221', pluginId: 4).save();         // url: '/invReport/downloadSupplierChalanCsvReport'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-201', pluginId: 4).save();         // url: '/invInventoryTransactionDetails/acknowledgeInvoiceFromSupplier'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-202', pluginId: 4).save();         // url: '/invReport/downloadSupplierChalanReport'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-221', pluginId: 4).save();         // url: '/invReport/downloadSupplierChalanCsvReport'

        // inventory status with value report

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-204', pluginId: 4).save();         // url: '/invReport/showInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-205', pluginId: 4).save();         // url: '/invReport/listInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-206', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-218', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithValueCsv'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-204', pluginId: 4).save();         // url: '/invReport/showInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-205', pluginId: 4).save();         // url: '/invReport/listInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-206', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-218', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithValueCsv'

        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-204', pluginId: 4).save();         // url: '/invReport/showInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-205', pluginId: 4).save();         // url: '/invReport/listInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-206', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-218', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithValueCsv'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-204', pluginId: 4).save();         // url: '/invReport/showInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-205', pluginId: 4).save();         // url: '/invReport/listInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-206', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-218', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithValueCsv'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-204', pluginId: 4).save();         // url: '/invReport/showInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-205', pluginId: 4).save();         // url: '/invReport/listInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-206', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-218', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithValueCsv'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-204', pluginId: 4).save();         // url: '/invReport/showInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-205', pluginId: 4).save();         // url: '/invReport/listInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-206', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-218', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithValueCsv'

        // inventory status report with Quantity

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-207', pluginId: 4).save();         // url: '/invReport/showInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-208', pluginId: 4).save();         // url: '/invReport/listInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-209', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-219', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantityCsv'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-207', pluginId: 4).save();         // url: '/invReport/showInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-208', pluginId: 4).save();         // url: '/invReport/listInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-209', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-219', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantityCsv'

        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-207', pluginId: 4).save();         // url: '/invReport/showInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-208', pluginId: 4).save();         // url: '/invReport/listInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-209', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-219', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantityCsv'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-207', pluginId: 4).save();         // url: '/invReport/showInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-208', pluginId: 4).save();         // url: '/invReport/listInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-209', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-219', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantityCsv'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-207', pluginId: 4).save();         // url: '/invReport/showInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-208', pluginId: 4).save();         // url: '/invReport/listInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-209', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-219', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantityCsv'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-207', pluginId: 4).save();         // url: '/invReport/showInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-208', pluginId: 4).save();         // url: '/invReport/listInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-209', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-219', pluginId: 4).save();         // url: '/invReport/downloadInventoryStatusWithQuantityCsv'

        // Item-Reconciliation Report

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-214', pluginId: 4).save();         // url: '/invReport/showForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-215', pluginId: 4).save();         // url: '/invReport/listForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-216', pluginId: 4).save();         // url: '/invReport/downloadForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'INV-225', pluginId: 4).save();         // url: '/invReport/downloadForItemReconciliationCsv'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-214', pluginId: 4).save();         // url: '/invReport/showForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-215', pluginId: 4).save();         // url: '/invReport/listForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-216', pluginId: 4).save();         // url: '/invReport/downloadForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'INV-225', pluginId: 4).save();         // url: '/invReport/downloadForItemReconciliationCsv'

        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-214', pluginId: 4).save();         // url: '/invReport/showForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-215', pluginId: 4).save();         // url: '/invReport/listForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-216', pluginId: 4).save();         // url: '/invReport/downloadForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: -6, transactionCode: 'INV-225', pluginId: 4).save();         // url: '/invReport/downloadForItemReconciliationCsv'

        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-214', pluginId: 4).save();         // url: '/invReport/showForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-215', pluginId: 4).save();         // url: '/invReport/listForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-216', pluginId: 4).save();         // url: '/invReport/downloadForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'INV-225', pluginId: 4).save();         // url: '/invReport/downloadForItemReconciliationCsv'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-214', pluginId: 4).save();         // url: '/invReport/showForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-215', pluginId: 4).save();         // url: '/invReport/listForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-216', pluginId: 4).save();         // url: '/invReport/downloadForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-225', pluginId: 4).save();         // url: '/invReport/downloadForItemReconciliationCsv'

        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-214', pluginId: 4).save();         // url: '/invReport/showForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-215', pluginId: 4).save();         // url: '/invReport/listForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-216', pluginId: 4).save();         // url: '/invReport/downloadForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: -11, transactionCode: 'INV-225', pluginId: 4).save();         // url: '/invReport/downloadForItemReconciliationCsv'

        return true
    }

    public boolean createRoleFeatureMapForFixedAssetPlugin() {
        // default
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'FA-47', pluginId: 7).save();    //   url: '/fixedAsset/renderFixedAssetMenu'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-47', pluginId: 7).save();    //   url: '/fixedAsset/renderFixedAssetMenu'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-47', pluginId: 7).save();    //   url: '/fixedAsset/renderFixedAssetMenu'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-47', pluginId: 7).save();   //   url: '/fixedAsset/renderFixedAssetMenu'

        // for Fixed Asset Details
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-1', pluginId: 7).save();    //   url: '/fxdFixedAssetDetails/show'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-2', pluginId: 7).save();    //   url: '/fxdFixedAssetDetails/create'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-3', pluginId: 7).save();    //   url: '/fxdFixedAssetDetails/delete'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-4', pluginId: 7).save();    //   url: '/fxdFixedAssetDetails/list'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-5', pluginId: 7).save();    //   url: '/fxdFixedAssetDetails/select'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-6', pluginId: 7).save();    //   url: '/fxdFixedAssetDetails/update'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-7', pluginId: 7).save();    //   url: '/fxdFixedAssetDetails/getFixedAssetList'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-1', pluginId: 7).save();    //   url: '/fxdFixedAssetDetails/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-2', pluginId: 7).save();    //   url: '/fxdFixedAssetDetails/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-3', pluginId: 7).save();    //   url: '/fxdFixedAssetDetails/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-4', pluginId: 7).save();    //   url: '/fxdFixedAssetDetails/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-5', pluginId: 7).save();    //   url: '/fxdFixedAssetDetails/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-6', pluginId: 7).save();    //   url: '/fxdFixedAssetDetails/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-7', pluginId: 7).save();    //   url: '/fxdFixedAssetDetails/getFixedAssetList'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-1', pluginId: 7).save();    //   url: '/fxdFixedAssetDetails/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-2', pluginId: 7).save();    //   url: '/fxdFixedAssetDetails/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-3', pluginId: 7).save();    //   url: '/fxdFixedAssetDetails/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-4', pluginId: 7).save();    //   url: '/fxdFixedAssetDetails/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-5', pluginId: 7).save();    //   url: '/fxdFixedAssetDetails/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-6', pluginId: 7).save();    //   url: '/fxdFixedAssetDetails/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-7', pluginId: 7).save();    //   url: '/fxdFixedAssetDetails/getFixedAssetList'

        // For Fixed Asset Trace
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-16', pluginId: 7).save();    //   url: '/fxdFixedAssetTrace/show'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-17', pluginId: 7).save();    //   url: '/fxdFixedAssetTrace/list'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-18', pluginId: 7).save();    //   url: '/fxdFixedAssetTrace/create'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-19', pluginId: 7).save();    //   url: '/fxdFixedAssetTrace/getItemList'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-16', pluginId: 7).save();    //   url: '/fxdFixedAssetTrace/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-17', pluginId: 7).save();    //   url: '/fxdFixedAssetTrace/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-18', pluginId: 7).save();    //   url: '/fxdFixedAssetTrace/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-19', pluginId: 7).save();    //   url: '/fxdFixedAssetTrace/getItemList'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-16', pluginId: 7).save();    //   url: '/fxdFixedAssetTrace/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-17', pluginId: 7).save();    //   url: '/fxdFixedAssetTrace/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-18', pluginId: 7).save();    //   url: '/fxdFixedAssetTrace/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-19', pluginId: 7).save();    //   url: '/fxdFixedAssetTrace/getItemList'
        // for Maintenance Type
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-24', pluginId: 7).save();    //   url: '/fxdMaintenanceType/show'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-25', pluginId: 7).save();    //   url: '/fxdMaintenanceType/create'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-26', pluginId: 7).save();    //   url: '/fxdMaintenanceType/delete'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-27', pluginId: 7).save();    //   url: '/fxdMaintenanceType/list'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-28', pluginId: 7).save();    //   url: '/fxdMaintenanceType/select'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-29', pluginId: 7).save();    //   url: '/fxdMaintenanceType/update'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-24', pluginId: 7).save();    //   url: '/fxdMaintenanceType/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-25', pluginId: 7).save();    //   url: '/fxdMaintenanceType/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-26', pluginId: 7).save();    //   url: '/fxdMaintenanceType/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-27', pluginId: 7).save();    //   url: '/fxdMaintenanceType/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-28', pluginId: 7).save();    //   url: '/fxdMaintenanceType/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-29', pluginId: 7).save();    //   url: '/fxdMaintenanceType/update'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-24', pluginId: 7).save();    //   url: '/fxdMaintenanceType/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-25', pluginId: 7).save();    //   url: '/fxdMaintenanceType/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-26', pluginId: 7).save();    //   url: '/fxdMaintenanceType/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-27', pluginId: 7).save();    //   url: '/fxdMaintenanceType/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-28', pluginId: 7).save();    //   url: '/fxdMaintenanceType/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-29', pluginId: 7).save();    //   url: '/fxdMaintenanceType/update'
        // for Category Maintenance Type
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-30', pluginId: 7).save();    //   url: '/fxdCategoryMaintenanceType/show'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-31', pluginId: 7).save();    //   url: '/fxdCategoryMaintenanceType/create'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-32', pluginId: 7).save();    //   url: '/fxdCategoryMaintenanceType/delete'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-33', pluginId: 7).save();    //   url: '/fxdCategoryMaintenanceType/list'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-34', pluginId: 7).save();    //   url: '/fxdCategoryMaintenanceType/select'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-35', pluginId: 7).save();    //   url: '/fxdCategoryMaintenanceType/update'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-54', pluginId: 7).save();    //   url: '/fxdCategoryMaintenanceType/dropDownFxdMaintenanceTypeReload'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-30', pluginId: 7).save();    //   url: '/fxdCategoryMaintenanceType/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-31', pluginId: 7).save();    //   url: '/fxdCategoryMaintenanceType/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-32', pluginId: 7).save();    //   url: '/fxdCategoryMaintenanceType/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-33', pluginId: 7).save();    //   url: '/fxdCategoryMaintenanceType/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-34', pluginId: 7).save();    //   url: '/fxdCategoryMaintenanceType/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-35', pluginId: 7).save();    //   url: '/fxdCategoryMaintenanceType/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-54', pluginId: 7).save();    //   url: '/fxdCategoryMaintenanceType/dropDownFxdMaintenanceTypeReload'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-30', pluginId: 7).save();    //   url: '/fxdCategoryMaintenanceType/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-31', pluginId: 7).save();    //   url: '/fxdCategoryMaintenanceType/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-32', pluginId: 7).save();    //   url: '/fxdCategoryMaintenanceType/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-33', pluginId: 7).save();    //   url: '/fxdCategoryMaintenanceType/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-34', pluginId: 7).save();    //   url: '/fxdCategoryMaintenanceType/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-35', pluginId: 7).save();    //   url: '/fxdCategoryMaintenanceType/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-54', pluginId: 7).save();    //   url: '/fxdCategoryMaintenanceType/dropDownFxdMaintenanceTypeReload'
        // for Maintenance
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-40', pluginId: 7).save();    //   url: '/fxdMaintenance/show'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-41', pluginId: 7).save();    //   url: '/fxdMaintenance/create'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-42', pluginId: 7).save();    //   url: '/fxdMaintenance/delete'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-43', pluginId: 7).save();    //   url: '/fxdMaintenance/list'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-44', pluginId: 7).save();    //   url: '/fxdMaintenance/select'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-45', pluginId: 7).save();    //   url: '/fxdMaintenance/update'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-46', pluginId: 7).save();    //   url: '/fxdMaintenance/getMaintenanceTypeAndModelListByItemId'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-40', pluginId: 7).save();    //   url: '/fxdMaintenance/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-41', pluginId: 7).save();    //   url: '/fxdMaintenance/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-42', pluginId: 7).save();    //   url: '/fxdMaintenance/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-43', pluginId: 7).save();    //   url: '/fxdMaintenance/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-44', pluginId: 7).save();    //   url: '/fxdMaintenance/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-45', pluginId: 7).save();    //   url: '/fxdMaintenance/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-46', pluginId: 7).save();    //   url: '/fxdMaintenance/getMaintenanceTypeAndModelListByItemId'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-40', pluginId: 7).save();    //   url: '/fxdMaintenance/show'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-41', pluginId: 7).save();    //   url: '/fxdMaintenance/create'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-42', pluginId: 7).save();    //   url: '/fxdMaintenance/delete'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-43', pluginId: 7).save();    //   url: '/fxdMaintenance/list'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-44', pluginId: 7).save();    //   url: '/fxdMaintenance/select'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-45', pluginId: 7).save();    //   url: '/fxdMaintenance/update'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-46', pluginId: 7).save();    //   url: '/fxdMaintenance/getMaintenanceTypeAndModelListByItemId'

        // Fixed Asset Report

        // Consumption Against Asset
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-9', pluginId: 7).save();    //   url: '/fixedAssetReport/showConsumptionAgainstAsset'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-10', pluginId: 7).save();    //   url: '/fixedAssetReport/listConsumptionAgainstAsset'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-11', pluginId: 7).save();    //   url: '/fixedAssetReport/getConsumptionAgainstAssetDetails'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-12', pluginId: 7).save();    //   url: '/fixedAssetReport/downloadConsumptionAgainstAsset'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-53', pluginId: 7).save();    //   url: '/fixedAssetReport/downloadConsumptionAgainstAssetDetailsCsv'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-9', pluginId: 7).save();    //   url: '/fixedAssetReport/showConsumptionAgainstAsset'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-10', pluginId: 7).save();    //   url: '/fixedAssetReport/listConsumptionAgainstAsset'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-11', pluginId: 7).save();    //   url: '/fixedAssetReport/getConsumptionAgainstAssetDetails'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-12', pluginId: 7).save();    //   url: '/fixedAssetReport/downloadConsumptionAgainstAsset'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-53', pluginId: 7).save();    //   url: '/fixedAssetReport/downloadConsumptionAgainstAssetDetailsCsv'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-9', pluginId: 7).save();    //   url: '/fixedAssetReport/showConsumptionAgainstAsset'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-10', pluginId: 7).save();    //   url: '/fixedAssetReport/listConsumptionAgainstAsset'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-11', pluginId: 7).save();    //   url: '/fixedAssetReport/getConsumptionAgainstAssetDetails'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-12', pluginId: 7).save();    //   url: '/fixedAssetReport/downloadConsumptionAgainstAsset'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-53', pluginId: 7).save();    //   url: '/fixedAssetReport/downloadConsumptionAgainstAssetDetailsCsv'

        // Pending Fixed Asset
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-13', pluginId: 7).save();    //   url: '/fixedAssetReport/showPendingFixedAsset'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-14', pluginId: 7).save();    //   url: '/fixedAssetReport/listPendingFixedAsset'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-15', pluginId: 7).save();    //   url: '/fixedAssetReport/downloadPendingFixedAsset'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-13', pluginId: 7).save();    //   url: '/fixedAssetReport/showPendingFixedAsset'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-14', pluginId: 7).save();    //   url: '/fixedAssetReport/listPendingFixedAsset'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-15', pluginId: 7).save();    //   url: '/fixedAssetReport/downloadPendingFixedAsset'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-13', pluginId: 7).save();    //   url: '/fixedAssetReport/showPendingFixedAsset'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-14', pluginId: 7).save();    //   url: '/fixedAssetReport/listPendingFixedAsset'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-15', pluginId: 7).save();    //   url: '/fixedAssetReport/downloadPendingFixedAsset'

        // Current Fixed Asset
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-20', pluginId: 7).save();    //   url: '/fixedAssetReport/showCurrentFixedAsset'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-21', pluginId: 7).save();    //   url: '/fixedAssetReport/listCurrentFixedAsset'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-22', pluginId: 7).save();    // url: '/fixedAssetReport/searchCurrentFixedAsset'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-23', pluginId: 7).save();    //  url: '/fixedAssetReport/downloadCurrentFixedAsset'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-51', pluginId: 7).save();    //   url: '/fixedAssetReport/downloadCurrentFixedAssetCsv'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-20', pluginId: 7).save();    //   url: '/fixedAssetReport/showCurrentFixedAsset'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-21', pluginId: 7).save();    //   url: '/fixedAssetReport/listCurrentFixedAsset'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-22', pluginId: 7).save();    //   url: '/fixedAssetReport/searchCurrentFixedAsset'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-23', pluginId: 7).save();    //  url: '/fixedAssetReport/downloadCurrentFixedAsset'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-51', pluginId: 7).save();    //   url: '/fixedAssetReport/downloadCurrentFixedAssetCsv'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-20', pluginId: 7).save();    //   url: '/fixedAssetReport/showCurrentFixedAsset'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-21', pluginId: 7).save();    //   url: '/fixedAssetReport/listCurrentFixedAsset'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-22', pluginId: 7).save();    //  url: '/fixedAssetReport/searchCurrentFixedAsset'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-23', pluginId: 7).save();    //  url: '/fixedAssetReport/downloadCurrentFixedAsset'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-51', pluginId: 7).save();    //   url: '/fixedAssetReport/downloadCurrentFixedAssetCsv'

        // Consumption Against Asset Details
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-48', pluginId: 7).save();    //   url: '/fixedAssetReport/showConsumptionAgainstAssetDetails'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-49', pluginId: 7).save();    //   url: '/fixedAssetReport/listConsumptionAgainstAssetDetails'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'FA-50', pluginId: 7).save();    //   url: '/fixedAssetReport/downloadConsumptionAgainstAssetDetails'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-48', pluginId: 7).save();    //   url: '/fixedAssetReport/showConsumptionAgainstAssetDetails'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-49', pluginId: 7).save();    //   url: '/fixedAssetReport/listConsumptionAgainstAssetDetails'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'FA-50', pluginId: 7).save();    //   url: '/fixedAssetReport/downloadConsumptionAgainstAssetDetails'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-48', pluginId: 7).save();    //   url: '/fixedAssetReport/showConsumptionAgainstAssetDetails'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-49', pluginId: 7).save();    //   url: '/fixedAssetReport/listConsumptionAgainstAssetDetails'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'FA-50', pluginId: 7).save();    //   url: '/fixedAssetReport/downloadConsumptionAgainstAssetDetails'

        return true
    }

    public boolean createRoleFeatureMapForQSPlugin() {
        // default
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'QS-41', pluginId: 6).save();   //   url: '/qs/renderQSMenu'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-41', pluginId: 6).save();   //   url: '/qs/renderQSMenu'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-41', pluginId: 6).save();   //   url: '/qs/renderQSMenu'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'QS-41', pluginId: 6).save();  //   url: '/qs/renderQSMenu'

        // qs Measurement
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-1', pluginId: 6).save();    //   url: '/qsMeasurement/show'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-2', pluginId: 6).save();    //   url: '/qsMeasurement/create'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-3', pluginId: 6).save();    //   url: '/qsMeasurement/select'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-4', pluginId: 6).save();    //   url: '/qsMeasurement/update'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-5', pluginId: 6).save();    //   url: '/qsMeasurement/delete'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-6', pluginId: 6).save();    //   url: '/qsMeasurement/list'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-7', pluginId: 6).save();    //   url: '/qsMeasurement/showGovt'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-40', pluginId: 6).save();   //   url: '/qsMeasurement/getQsStatusForDashBoard'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-1', pluginId: 6).save();    //   url: '/qsMeasurement/show'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-2', pluginId: 6).save();    //   url: '/qsMeasurement/create'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-3', pluginId: 6).save();    //   url: '/qsMeasurement/select'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-4', pluginId: 6).save();    //   url: '/qsMeasurement/update'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-5', pluginId: 6).save();    //   url: '/qsMeasurement/delete'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-6', pluginId: 6).save();    //   url: '/qsMeasurement/list'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-7', pluginId: 6).save();    //   url: '/qsMeasurement/showGovt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-40', pluginId: 6).save();   //   url: '/qsMeasurement/getQsStatusForDashBoard'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'QS-40', pluginId: 6).save();  //   url: '/qsMeasurement/getQsStatusForDashBoard'

        // budget Contract details report
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-15', pluginId: 6).save();    //   url: '/qsReport/showBudgetContractDetails'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-16', pluginId: 6).save();    //   url: '/qsReport/listBudgetContractDetails'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-23', pluginId: 6).save();    //   url: '/qsReport/downloadBudgetContractDetails'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-42', pluginId: 6).save();    //   url: '/qsReport/downloadBudgetContractCsvDetails'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-15', pluginId: 6).save();    //   url: '/qsReport/showBudgetContractDetails'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-16', pluginId: 6).save();    //   url: '/qsReport/listBudgetContractDetails'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-23', pluginId: 6).save();    //   url: '/qsReport/downloadBudgetContractDetails'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-42', pluginId: 6).save();    //   url: '/qsReport/downloadBudgetContractCsvDetails'
        // budget financial summary report
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-17', pluginId: 6).save();    //   url: '/qsReport/showBudgetFinancialSummary'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-18', pluginId: 6).save();    //   url: '/qsReport/listBudgetFinancialSummary'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-24', pluginId: 6).save();    //   url: '/qsReport/downloadBudgetFinancialSummary'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-43', pluginId: 6).save();    //   url: '/qsReport/downloadBudgetFinancialCsvSummary'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-17', pluginId: 6).save();    //   url: '/qsReport/showBudgetFinancialSummary'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-18', pluginId: 6).save();    //   url: '/qsReport/listBudgetFinancialSummary'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-24', pluginId: 6).save();    //   url: '/qsReport/downloadBudgetFinancialSummary'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-43', pluginId: 6).save();    //   url: '/qsReport/downloadBudgetFinancialCsvSummary'
        // get Budget for QS
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-19', pluginId: 6).save();    //   url: '/qsMeasurement/getBudgetForQS'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-19', pluginId: 6).save();    //   url: '/qsMeasurement/getBudgetForQS'

        // QS Measurement report
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-13', pluginId: 6).save();    //   url: '/qsReport/showQsMeasurementRpt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-13', pluginId: 6).save();    //   url: '/qsReport/listQsMeasurementRpt'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'QS-13', pluginId: 6).save();    //   url: '/qsReport/downloadQsMeasurementRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'QS-13', pluginId: 6).save();   //   url: '/qsReport/downloadQsMeasurementCsvRpt'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-14', pluginId: 6).save();    //   url: '/qsReport/showQsMeasurementRpt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-14', pluginId: 6).save();    //   url: '/qsReport/listQsMeasurementRpt'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'QS-14', pluginId: 6).save();    //   url: '/qsReport/downloadQsMeasurementRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'QS-14', pluginId: 6).save();   //   url: '/qsReport/downloadQsMeasurementCsvRpt'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-33', pluginId: 6).save();    //   url: '/qsReport/showQsMeasurementRpt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-33', pluginId: 6).save();    //   url: '/qsReport/listQsMeasurementRpt'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'QS-33', pluginId: 6).save();    //   url: '/qsReport/downloadQsMeasurementRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'QS-33', pluginId: 6).save();   //   url: '/qsReport/downloadQsMeasurementCsvRpt'

        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-45', pluginId: 6).save();    //   url: '/qsReport/showQsMeasurementRpt'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-45', pluginId: 6).save();    //   url: '/qsReport/listQsMeasurementRpt'
        new RoleFeatureMapping(roleTypeId: -8, transactionCode: 'QS-45', pluginId: 6).save();    //   url: '/qsReport/downloadQsMeasurementRpt'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'QS-45', pluginId: 6).save();   //   url: '/qsReport/downloadQsMeasurementCsvRpt'

        // budget wise qs report
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-20', pluginId: 6).save();    //   url: '/qsReport/showBudgetWiseQs'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-21', pluginId: 6).save();    //   url: '/qsReport/listBudgetWiseQs'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-22', pluginId: 6).save();    //   url: '/qsReport/downloadBudgetWiseQs'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-46', pluginId: 6).save();    //   url: '/qsReport/downloadBudgetWiseQsCsv'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-20', pluginId: 6).save();    //   url: '/qsReport/showBudgetWiseQs'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-21', pluginId: 6).save();    //   url: '/qsReport/listBudgetWiseQs'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-22', pluginId: 6).save();    //   url: '/qsReport/downloadBudgetWiseQs'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-46', pluginId: 6).save();    //   url: '/qsReport/downloadBudgetWiseQsCsv'

        // Combined QS Measurement Report
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-30', pluginId: 6).save();    //   url: '/qsReport/showCombinedQSM'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-31', pluginId: 6).save();    //   url: '/qsReport/listCombinedQSM'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-32', pluginId: 6).save();    //   url: '/qsReport/downloadCombinedQSM'
        new RoleFeatureMapping(roleTypeId: -4, transactionCode: 'QS-44', pluginId: 6).save();    //   url: '/qsReport/downloadCombinedQSMCsv'

        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-30', pluginId: 6).save();    //   url: '/qsReport/showCombinedQSM'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-31', pluginId: 6).save();    //   url: '/qsReport/listCombinedQSM'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-32', pluginId: 6).save();    //   url: '/qsReport/downloadCombinedQSM'
        new RoleFeatureMapping(roleTypeId: -5, transactionCode: 'QS-44', pluginId: 6).save();    //   url: '/qsReport/downloadCombinedQSMCsv'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'QS-30', pluginId: 6).save();   //   url: '/qsReport/showCombinedQSM'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'QS-31', pluginId: 6).save();   //   url: '/qsReport/listCombinedQSM'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'QS-32', pluginId: 6).save();   //   url: '/qsReport/downloadCombinedQSM'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'QS-44', pluginId: 6).save();   //   url: '/qsReport/downloadCombinedQSMCsv'

        return true
    }

    public boolean createRoleFeatureMapForExchangeHousePlugin() {
        // default
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-160', pluginId: 9).save();        // url: 'exhExchangeHouse/renderExchangeHouseMenu'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-160', pluginId: 9).save();      // url: 'exhExchangeHouse/renderExchangeHouseMenu'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-160', pluginId: 9).save();      // url: 'exhExchangeHouse/renderExchangeHouseMenu'
        new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-160', pluginId: 9).save();      // url: 'exhExchangeHouse/renderExchangeHouseMenu'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-160', pluginId: 9).save();      // url: 'exhExchangeHouse/renderExchangeHouseMenu'

        // customer
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-1', pluginId: 9).save();      // url: 'exhCustomer/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-2', pluginId: 9).save();        // url: 'exhCustomer/showForAdmin'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-3', pluginId: 9).save();      // url: 'exhCustomer/create'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-4', pluginId: 9).save();      // url: 'exhCustomer/update'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-5', pluginId: 9).save();      // url: 'exhCustomer/edit'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-6', pluginId: 9).save();        // url: 'exhCustomer/list'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-6', pluginId: 9).save();      // url: 'exhCustomer/list'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-7', pluginId: 9).save();      // url: 'exhCustomer/delete'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-8', pluginId: 9).save();      // url: 'exhCustomer/showCustomerUser'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-9', pluginId: 9).save();        // url: 'exhCustomer/searchCustomerUser'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-9', pluginId: 9).save();      // url: 'exhCustomer/searchCustomerUser'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-10', pluginId: 9).save();     // url: 'exhCustomer/createCustomerUser'

        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-3', pluginId: 9).save();      // url: 'exhCustomer/create'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-4', pluginId: 9).save();      // url: 'exhCustomer/update'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-5', pluginId: 9).save();      // url: 'exhCustomer/edit'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-7', pluginId: 9).save();      // url: 'exhCustomer/delete'

        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-133', pluginId: 9).save();      // url: '/exhCustomer/showForAgent'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-136', pluginId: 9).save();      // url: '/exhCustomer/listForAgent'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-151', pluginId: 9).save();      // url: '/exhCustomer/showForCustomerByNameAndCode'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-152', pluginId: 9).save();      // url: '/exhCustomer/searchForCustomerByNameAndCode'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-151', pluginId: 9).save();      // url: '/exhCustomer/showForCustomerByNameAndCode'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-152', pluginId: 9).save();      // url: '/exhCustomer/searchForCustomerByNameAndCode'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-260', pluginId: 9).save();       //url '/exhCustomer/reloadCustomerDetailsForAdmin'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-260', pluginId: 9).save();       //url '/exhCustomer/reloadCustomerDetailsForAdmin'

        //block customer for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-257', pluginId: 9).save();      // url: '/exhCustomer/blockExhCustomer'
        // block customer for cashier
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-257', pluginId: 9).save();      // url: '/exhCustomer/blockExhCustomer'
        //unblock customer for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-259', pluginId: 9).save();      // url: '/exhCustomer/unblockExhCustomer'

        // Customer Registration
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-186', pluginId: 9).save();      // url: '/exhCustomer/displayPhotoIdImage'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-186', pluginId: 9).save();    // url: '/exhCustomer/displayPhotoIdImage'

        // Beneficiary
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-11', pluginId: 9).save();      // url: '/exhBeneficiary/show'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-11', pluginId: 9).save();      // url: '/exhBeneficiary/show'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-12', pluginId: 9).save();      // url: '/exhBeneficiary/create'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-12', pluginId: 9).save();      // url: '/exhBeneficiary/create'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-13', pluginId: 9).save();      // url: '/exhBeneficiary/update'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-13', pluginId: 9).save();      // url: '/exhBeneficiary/update'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-13', pluginId: 9).save();      // url: '/exhBeneficiary/update'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-14', pluginId: 9).save();      // url: '/exhBeneficiary/edit'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-14', pluginId: 9).save();      // url: '/exhBeneficiary/edit'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-14', pluginId: 9).save();      // url: '/exhBeneficiary/edit'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-15', pluginId: 9).save();      // url: '/exhBeneficiary/list'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-15', pluginId: 9).save();      // url: '/exhBeneficiary/list'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-16', pluginId: 9).save();      // url: '/exhBeneficiary/showNewForCustomer'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-17', pluginId: 9).save();      // url: '/exhBeneficiary/detailsForCustomer'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-18', pluginId: 9).save();      // url: '/exhBeneficiary/listForCustomer'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-19', pluginId: 9).save();      // url: '/exhBeneficiary/selectForCustomer'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-232', pluginId: 9).save();      // url: '/exhBeneficiary/showApprovedForCustomer
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-233', pluginId: 9).save();      // url: '/exhBeneficiary/createForCustomer'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-234', pluginId: 9).save();      // url: '/exhBeneficiary/updateForCustomer'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-255', pluginId: 9).save();      // url: '/exhBeneficiary/approveBeneficiary'

        // Task
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-170', pluginId: 9).save();        // url: '/exhTask/showExhTaskForAdmin'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-171', pluginId: 9).save();        // url: '/exhTask/showAgentTaskForAdmin'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-172', pluginId: 9).save();        // url: '/exhTask/showCustomerTaskForAdmin'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-176', pluginId: 9).save();      // url: '/exhTask/showExhTaskForCashier'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-177', pluginId: 9).save();      // url: '/exhTask/showAgentTaskForCashier'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-178', pluginId: 9).save();      // url: '/exhTask/showCustomerTaskForCashier'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-21', pluginId: 9).save();       // url: '/exhTask/create'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-21', pluginId: 9).save();       // url: '/exhTask/create'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-21', pluginId: 9).save();       // url: '/exhTask/create'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-22', pluginId: 9).save();       // url: '/exhTask/update'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-22', pluginId: 9).save();       // url: '/exhTask/update'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-22', pluginId: 9).save();       // url: '/exhTask/update'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-23', pluginId: 9).save();       // url: '/exhTask/edit'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-23', pluginId: 9).save();       // url: '/exhTask/edit'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-23', pluginId: 9).save();       // url: '/exhTask/edit'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-24', pluginId: 9).save();       // url: '/exhTask/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-173', pluginId: 9).save();        // url: '/exhTask/listExhTaskForAdmin'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-174', pluginId: 9).save();        // url: '/exhTask/listAgentTaskForAdmin'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-175', pluginId: 9).save();        // url: '/exhTask/listCustomerTaskForAdmin'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-179', pluginId: 9).save();      // url: '/exhTask/listExhTaskForCashier'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-180', pluginId: 9).save();      // url: '/exhTask/listAgentTaskForCashier'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-181', pluginId: 9).save();      // url: '/exhTask/listCustomerTaskForCashier'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-182', pluginId: 9).save();      // url: '/exhTask/approveTaskForCashier'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-26', pluginId: 9).save();         // url: '/exhTask/showForTaskSearch'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-26', pluginId: 9).save();       // url: '/exhTask/showForTaskSearch'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-26', pluginId: 9).save();       // url: '/exhTask/showForTaskSearch'
        new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-26', pluginId: 9).save();       // url: '/exhTask/showForTaskSearch'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-165', pluginId: 9).save();      // url: '/exhTask/showForTaskSearchForAgent'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-166', pluginId: 9).save();      // url: '/exhTask/searchTaskWithRefOrPinForAgent'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-196', pluginId: 9).save();      // url: '/exhTask/searchTaskWithRefOrPinForCustomer'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-134', pluginId: 9).save();      // url: '/exhTask/showForAgent'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-135', pluginId: 9).save();      // url: '/exhTask/listForAgent'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-138', pluginId: 9).save();      // url: '/exhTask/editForAgent'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-185', pluginId: 9).save();      // url: '/exhTask/calculateFeesAndCommission'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-185', pluginId: 9).save();      // url: '/exhTask/calculateFeesAndCommission'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-185', pluginId: 9).save();      // url: '/exhTask/calculateFeesAndCommission'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-185', pluginId: 9).save();      // url: '/exhTask/calculateFeesAndCommission'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-137', pluginId: 9).save();      // url: '/exhTask/sendToExchangeHouse'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-168', pluginId: 9).save();      // url: '/exhTask/sendToExhForCustomer'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-239', pluginId: 9).save();      // url: '/exhTask/showForMakePayment'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-261', pluginId: 9).save();      // url: '/exhTask/reloadShowTaskDetailsTagLib'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-261', pluginId: 9).save();      // url: '/exhTask/reloadShowTaskDetailsTagLib'

        // task for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-29', pluginId: 9).save();      // url: '/exhTask/sendToBank'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-30', pluginId: 9).save();      // url: '/exhTask/cancelSpecificTask'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-31', pluginId: 9).save();      // url: '/exhTask/showTaskDetails'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-31', pluginId: 9).save();    // url: '/exhTask/showTaskDetails'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-32', pluginId: 9).save();      // url: '/exhTask/showTaskDetailsForAdmin'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-33', pluginId: 9).save();      // url: '/exhTask/searchTaskWithRefOrPin'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-33', pluginId: 9).save();      // url: '/exhTask/searchTaskWithRefOrPin'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-33', pluginId: 9).save();      // url: '/exhTask/searchTaskWithRefOrPin'
        new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-33', pluginId: 9).save();      // url: '/exhTask/searchTaskWithRefOrPin'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-34', pluginId: 9).save();      // url: '/exhTask/showForCustomer'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-192', pluginId: 9).save();      // url: '/exhTask/showUnApprovedTaskForCustomer'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-193', pluginId: 9).save();      // url: '/exhTask/listUnApprovedTaskForCustomer'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-167', pluginId: 9).save();      // url: '/exhTask/showApprovedTaskForCustomer'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-169', pluginId: 9).save();      // url: '/exhTask/listApprovedTaskForCustomer'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-187', pluginId: 9).save();      // url: '/exhTask/showDisbursedTaskForCustomer'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-198', pluginId: 9).save();      // url: '/exhTask/listDisbursedTaskForCustomer'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-35', pluginId: 9).save();      // url: '/exhTask/listForCustomer'
        new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-36', pluginId: 9).save();      // url: '/exhTask/showForOtherBankUser'
        new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-37', pluginId: 9).save();      // url: '/exhTask/listForOtherBankUser'
        new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-38', pluginId: 9).save();      // url: '/exhTask/resolveTaskForOtherBank'
        new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-39', pluginId: 9).save();      // url: '/exhTask/downloadCsvForOtherBank'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'EXH-258', pluginId: 9).save();      // url: '/exhTask/showDetailsForReplaceTask'

        //bank drop down to reload independently
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-256', pluginId: 9).save();      //url: '/exhTask/reloadBankByTaskStatusAndTaskType'
        new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-256', pluginId: 9).save();      //url: '/exhTask/reloadBankByTaskStatusAndTaskType'

        // setting remitance purpose
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-56', pluginId: 9).save();      // url: '/exhRemittancePurpose/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-57', pluginId: 9).save();      // url: '/exhRemittancePurpose/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-58', pluginId: 9).save();      // url: '/exhRemittancePurpose/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-59', pluginId: 9).save();      // url: '/exhRemittancePurpose/edit'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-60', pluginId: 9).save();      // url: '/exhRemittancePurpose/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-61', pluginId: 9).save();      // url: '/exhRemittancePurpose/delete'

        // setting photo id type
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-62', pluginId: 9).save();      // url: '/exhPhotoIdType/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-63', pluginId: 9).save();      // url: '/exhPhotoIdType/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-64', pluginId: 9).save();      // url: '/exhPhotoIdType/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-65', pluginId: 9).save();      // url: '/exhPhotoIdType/edit'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-66', pluginId: 9).save();      // url: '/exhPhotoIdType/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-67', pluginId: 9).save();      // url: '/exhPhotoIdType/delete'



        // setting country
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-74', pluginId: 9).save();      // url: '/exhCountry/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-75', pluginId: 9).save();      // url: '/exhCountry/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-76', pluginId: 9).save();      // url: '/exhCountry/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-77', pluginId: 9).save();      // url: '/exhCountry/edit'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-78', pluginId: 9).save();      // url: '/exhCountry/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-79', pluginId: 9).save();      // url: '/exhCountry/delete'

        // setting currency conversion
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-95', pluginId: 9).save();      // url: '/exhCurrencyConversion/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-96', pluginId: 9).save();      // url: '/exhCurrencyConversion/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-97', pluginId: 9).save();      // url: '/exhCurrencyConversion/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-98', pluginId: 9).save();      // url: '/exhCurrencyConversion/edit'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-99', pluginId: 9).save();      // url: '/exhCurrencyConversion/list'

        // Setting Exh User Agent Mapping
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-153', pluginId: 9).save();      // url: '/exhUserAgent/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-154', pluginId: 9).save();      // url: '/exhUserAgent/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-155', pluginId: 9).save();      // url: '/exhUserAgent/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-156', pluginId: 9).save();      // url: '/exhUserAgent/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-157', pluginId: 9).save();      // url: '/exhUserAgent/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-158', pluginId: 9).save();      // url: '/exhUserAgent/select'

        // setting Exh Agent
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-145', pluginId: 9).save();      // url: '/exhAgent/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-146', pluginId: 9).save();      // url: '/exhAgent/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-147', pluginId: 9).save();      // url: '/exhAgent/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-148', pluginId: 9).save();      // url: '/exhAgent/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-149', pluginId: 9).save();      // url: '/exhAgent/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-150', pluginId: 9).save();      // url: '/exhAgent/delete'

        // setting Exh Agent Currency Posting
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-139', pluginId: 9).save();      // url: '/exhAgentCurrencyPosting/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-140', pluginId: 9).save();      // url: '/exhAgentCurrencyPosting/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-141', pluginId: 9).save();      // url: '/exhAgentCurrencyPosting/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-142', pluginId: 9).save();      // url: '/exhAgentCurrencyPosting/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-143', pluginId: 9).save();      // url: '/exhAgentCurrencyPosting/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-144', pluginId: 9).save();      // url: '/exhAgentCurrencyPosting/delete'

        // settings Exh Regular Fee
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'EXH-20', pluginId: 9).save();     // url: '/exhRegularFee/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-27', pluginId: 9).save();      // url: '/exhRegularFee/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-28', pluginId: 9).save();      // url: '/exhRegularFee/calculate'

        // sanction
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-124', pluginId: 9).save();      // url: '/exhSanction/sanctionCountFromBeneficiary'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-124', pluginId: 9).save();      // url: '/exhSanction/sanctionCountFromBeneficiary'

        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-125', pluginId: 9).save();      // url: '/exhSanction/sanctionCountFromCustomer'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-125', pluginId: 9).save();      // url: '/exhSanction/sanctionCountFromCustomer'

        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-126', pluginId: 9).save();      // url: '/exhSanction/showFromBeneficiary'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-126', pluginId: 9).save();      // url: '/exhSanction/showFromBeneficiary'

        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-127', pluginId: 9).save();      // url: '/exhSanction/showFromCustomer'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-127', pluginId: 9).save();      // url: '/exhSanction/showFromCustomer'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-128', pluginId: 9).save();        // url: '/exhSanction/show'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-129', pluginId: 9).save();        // url: '/exhSanction/list'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-129', pluginId: 9).save();      // url: '/exhSanction/list'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-129', pluginId: 9).save();      // url: '/exhSanction/list'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-129', pluginId: 9).save();      // url: '/exhSanction/list'

        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-130', pluginId: 9).save();      // url: '/exhSanction/listForCustomer'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-130', pluginId: 9).save();      // url: '/exhSanction/listForCustomer'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-131', pluginId: 9).save();      // url: '/exhSanction/showSanctionUpload'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-132', pluginId: 9).save();      // url: '/exhSanction/uploadSanctionFile'

        // Agent wise commission
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-188', pluginId: 9).save();      // url: '/exhReport/showAgentWiseCommissionForAdmin'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-189', pluginId: 9).save();      // url: '/exhReport/listAgentWiseCommissionForAdmin'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-190', pluginId: 9).save();    // url: '/exhReport/showAgentWiseCommissionForAgent'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-191', pluginId: 9).save();    // url: '/exhReport/listAgentWiseCommissionForAgent'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-195', pluginId: 9).save();      // url: '/exhReport/downloadAgentWiseCommissionForAdmin'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-194', pluginId: 9).save();    // url: '/exhReport/downloadAgentWiseCommissionForAgent'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-116', pluginId: 9).save();      // url: '/exhReport/showCashierWiseReportForAdmin'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-117', pluginId: 9).save();    // url: '/exhReport/showCashierWiseReportForCashier'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-118', pluginId: 9).save();      // url: '/exhReport/listCashierWiseReportForAdmin'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-119', pluginId: 9).save();    // url: '/exhReport/listCashierWiseReportForCashier'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-120', pluginId: 9).save();      // url: '/exhReport/showSummaryReportForAdmin'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-121', pluginId: 9).save();      // url: '/exhReport/listReportSummaryForAdmin'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-122', pluginId: 9).save();      // url: '/exhReport/downloadRemittanceSummaryReport'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-123', pluginId: 9).save();      // url: '/exhReport/downloadCashierWiseTaskReport'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-123', pluginId: 9).save();    // url: '/exhReport/downloadCashierWiseTaskReport'

        // Reports
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-107', pluginId: 9).save();      // url: '/exhReport/showCustomerHistory'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-107', pluginId: 9).save();    // url: '/exhReport/showCustomerHistory'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-204', pluginId: 9).save();      // url: '/exhReport/downloadCustomerHistory'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-204', pluginId: 9).save();    // url: '/exhReport/downloadCustomerHistory'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-108', pluginId: 9).save();      // url: '/exhReport/getForCustomerRemittance'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-108', pluginId: 9).save();    // url: '/exhReport/getForCustomerRemittance'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-109', pluginId: 9).save();      // url: '/exhReport/listForCustomerRemittance'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-109', pluginId: 9).save();    // url: '/exhReport/listForCustomerRemittance'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-110', pluginId: 9).save();      // url: '/exhReport/showRemittanceSummary'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-110', pluginId: 9).save();    // url: '/exhReport/showRemittanceSummary'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-111', pluginId: 9).save();      // url: '/exhReport/getRemittanceSummaryReport'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-111', pluginId: 9).save();    // url: '/exhReport/getRemittanceSummaryReport'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-112', pluginId: 9).save();      // url: '/exhReport/showInvoice'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-112', pluginId: 9).save();    // url: '/exhReport/showInvoice'

        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-200', pluginId: 9).save();    // url: '/exhReport/showInvoiceForCustomer'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-113', pluginId: 9).save();        // url: '/exhReport/showInvoiceFromTaskGrid'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-113', pluginId: 9).save();      // url: '/exhReport/showInvoiceFromTaskGrid'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-113', pluginId: 9).save();      // url: '/exhReport/showInvoiceFromTaskGrid'

        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-199', pluginId: 9).save();      // url: '/exhReport/showInvoiceFromGridForCustomer'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-114', pluginId: 9).save();        // url: '/exhReport/getInvoiceDetails'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-114', pluginId: 9).save();      // url: '/exhReport/getInvoiceDetails'

        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-201', pluginId: 9).save();      // url: '/exhReport/invoiceDetailsForCustomer'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-115', pluginId: 9).save();        // url: '/exhReport/downloadInvoice'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-115', pluginId: 9).save();      // url: '/exhReport/downloadInvoice'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-115', pluginId: 9).save();      // url: '/exhReport/downloadInvoice'

        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-202', pluginId: 9).save();      // url: '/exhReport/downloadInvoiceForCustomer'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-205', pluginId: 9).save();        // url: '/exhReport/showCustomerRemittanceSummary'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-205', pluginId: 9).save();      // url: '/exhReport/showCustomerRemittanceSummary'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-206', pluginId: 9).save();        // url: '/exhReport/downloadCustomerRemittanceSummary'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-206', pluginId: 9).save();      // url: '/exhReport/downloadCustomerRemittanceSummary'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-235', pluginId: 9).save();        // url: '/exhReport/downloadRemittanceTransactionCsv'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'EXH-237', pluginId: 9).save();       // url: '/exhReport/downloadCustomerCSV'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-254', pluginId: 9).save();       // url: '/exhReport/downloadCustomer'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'EXH-254', pluginId: 9).save();       // url: '/exhReport/downloadCustomer'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-251', pluginId: 9).save();        // url: '/exhReport/listTransactionSummary'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-252', pluginId: 9).save();        // url: '/exhReport/downloadTransactionSummary'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-253', pluginId: 9).save();        // url: '/exhReport/listRemittanceTransaction'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-234', pluginId: 9).save();        // url: '/exhReport/downloadRemittanceTransaction'



        // /exhBeneficiary/listLinkedBeneficiary
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-207', pluginId: 9).save();      // url: '/exhBeneficiary/listLinkedBeneficiary'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-207', pluginId: 9).save();      // url: '/exhBeneficiary/listLinkedBeneficiary'
        // /exhCustomerBeneficiary/create
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-208', pluginId: 9).save();      // url: '/exhCustomerBeneficiary/create'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-208', pluginId: 9).save();      // url: '/exhCustomerBeneficiary/create'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-208', pluginId: 9).save();      // url: '/exhCustomerBeneficiary/create'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-209', pluginId: 9).save();        // url: '/exhReport/listForCustomerRemittanceSummary'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-209', pluginId: 9).save();      // url: '/exhReport/listForCustomerRemittanceSummary'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-210', pluginId: 9).save();        // url: '/exhReport/listCustomerTransactionSummary'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-210', pluginId: 9).save();      // url: '/exhReport/listCustomerTransactionSummary'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-211', pluginId: 9).save();        // url: '/exhReport/downloadCustomerTransactionSummary'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-211', pluginId: 9).save();      // url: '/exhReport/downloadCustomerTransactionSummary'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-212', pluginId: 9).save();        // url: '/exhReport/showCustomerTransactionSummary'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-212', pluginId: 9).save();      // url: '/exhReport/showCustomerTransactionSummary'

        /*Customer Entity Note*/
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-213', pluginId: 9).save();      // url: '/exhCustomer/showEntityNote'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-213', pluginId: 9).save();    // url: '/exhCustomer/showEntityNote'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-214', pluginId: 9).save();      // url: '/exhCustomer/createEntityNote'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-214', pluginId: 9).save();    // url: '/exhCustomer/createEntityNote'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-215', pluginId: 9).save();      // url: '/exhCustomer/listEntityNote'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-215', pluginId: 9).save();    // url: '/exhCustomer/listEntityNote'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-216', pluginId: 9).save();      // url: '/exhCustomer/deleteEntityNote'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-217', pluginId: 9).save();      // url: '/exhCustomer/updateEntityNote'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-217', pluginId: 9).save();    // url: '/exhCustomer/updateEntityNote'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-218', pluginId: 9).save();      // url: '/exhCustomer/editEntityNote'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-218', pluginId: 9).save();    // url: '/exhCustomer/editEntityNote'

        // Task note
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-226', pluginId: 9).save();      // url: '/exhTask/showEntityNoteForTask'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-226', pluginId: 9).save();    // url: '/exhTask/showEntityNoteForTask'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-227', pluginId: 9).save();      // url: '/exhTask/createEntityNoteForTask'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-227', pluginId: 9).save();    // url: '/exhTask/createEntityNoteForTask'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-228', pluginId: 9).save();      // url: '/exhTask/listEntityNoteForTask'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-228', pluginId: 9).save();    // url: '/exhTask/listEntityNoteForTask'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-230', pluginId: 9).save();      // url: '/exhTask/deleteEntityNoteForTask'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-229', pluginId: 9).save();      // url: '/exhTask/selectEntityNoteForTask'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-229', pluginId: 9).save();    // url: '/exhTask/selectEntityNoteForTask'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-231', pluginId: 9).save();      // url: '/exhTask/updateEntityNoteForTask'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-231', pluginId: 9).save();    // url: '/exhTask/updateEntityNoteForTask'

        //entity content
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-144', pluginId: 9).save(); // url: '/entityContent/show'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-145', pluginId: 9).save(); // url: '/entityContent/select'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-146', pluginId: 9).save(); // url: '/entityContent/list'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-147', pluginId: 9).save(); // url: '/entityContent/update'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-148', pluginId: 9).save(); // url: '/entityContent/create'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-149', pluginId: 9).save(); // url: '/entityContent/delete'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-152', pluginId: 9).save(); // url: '/entityContent/downloadContent'

        // Customer Attachment/Content
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-219', pluginId: 9).save();      // url: '/exhCustomer/showEntityContent'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-219', pluginId: 9).save();    // url: '/exhCustomer/showEntityContent'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-220', pluginId: 9).save();      // url: '/exhCustomer/createEntityContent'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-220', pluginId: 9).save();    // url: '/exhCustomer/createEntityContent'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-221', pluginId: 9).save();      // url: '/exhCustomer/listEntityContent'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-221', pluginId: 9).save();    // url: '/exhCustomer/listEntityContent'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-222', pluginId: 9).save();      // url: '/exhCustomer/deleteEntityContent'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-223', pluginId: 9).save();      // url: '/exhCustomer/updateEntityContent'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-223', pluginId: 9).save();    // url: '/exhCustomer/updateEntityContent'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-224', pluginId: 9).save();      // url: '/exhCustomer/selectEntityContent'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-224', pluginId: 9).save();    // url: '/exhCustomer/selectEntityContent'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-225', pluginId: 9).save();      // url: '/exhCustomer/downloadEntityContent'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-225', pluginId: 9).save();    // url: '/exhCustomer/downloadEntityContent'

        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-153', pluginId: 9).save();    // url: '/contentCategory/listContentCategoryByContentTypeId'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-243', pluginId: 9).save();      // url: '/exhPostalCode/show
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-244', pluginId: 9).save();      // url: '/exhPostalCode/create
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-245', pluginId: 9).save();      // url: '/exhPostalCode/select
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-246', pluginId: 9).save();      // url: '/exhPostalCode/update
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-247', pluginId: 9).save();      // url: '/exhPostalCode/delete
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-248', pluginId: 9).save();      // url: '/exhPostalCode/list


        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-249', pluginId: 9).save();      // url: '/exhReport/showTransactionSummary
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'EXH-250', pluginId: 9).save();      // url: '/exhReport/showRemittanceTransaction

        //some application plugin request map
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-236', pluginId: 9).save();  //url: '/district/reloadDistrictDropDown'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'APP-236', pluginId: 9).save();  //url: '/district/reloadDistrictDropDown'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'APP-236', pluginId: 9).save();  //url: '/district/reloadDistrictDropDown'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-209', pluginId: 9).save(); //url: '/bankBranch/reloadBranchesDropDownByBankAndDistrict'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'APP-209', pluginId: 9).save(); //url: '/bankBranch/reloadBranchesDropDownByBankAndDistrict'
        new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'APP-209', pluginId: 9).save(); //url: '/bankBranch/reloadBranchesDropDownByBankAndDistrict'
        new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-210', pluginId: 9).save(); //url: '/bankBranch/listDistributionPoint'
        new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'APP-210', pluginId: 9).save(); //url: '/bankBranch/listDistributionPoint'

        return true
    }

    public boolean createRoleFeatureMapForProjectTrackPlugin(){
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-1', pluginId: 10).save();      // url: '/projectTrack/renderProjectTrackMenu'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-1', pluginId: 10).save();      // url: '/projectTrack/renderProjectTrackMenu'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-1', pluginId: 10).save();      // url: '/projectTrack/renderProjectTrackMenu'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-2', pluginId: 10).save();      // url: '/ptBacklog/show'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-2', pluginId: 10).save();      // url: '/ptBacklog/show'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-2', pluginId: 10).save();      // url: '/ptBacklog/show'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-3', pluginId: 10).save();      // url: '/ptBacklog/list'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-3', pluginId: 10).save();      // url: '/ptBacklog/list'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-3', pluginId: 10).save();      // url: '/ptBacklog/list'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-4', pluginId: 10).save();      // url: '/ptBacklog/select'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-4', pluginId: 10).save();      // url: '/ptBacklog/select'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-4', pluginId: 10).save();      // url: '/ptBacklog/select'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-5', pluginId: 10).save();      // url: '/ptBacklog/create'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-5', pluginId: 10).save();      // url: '/ptBacklog/create'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-5', pluginId: 10).save();      // url: '/ptBacklog/create'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-6', pluginId: 10).save();      // url: '/ptBacklog/update'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-6', pluginId: 10).save();      // url: '/ptBacklog/update'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-6', pluginId: 10).save();      // url: '/ptBacklog/update'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-7', pluginId: 10).save();      // url: '/ptBacklog/delete'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-7', pluginId: 10).save();      // url: '/ptBacklog/delete'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-7', pluginId: 10).save();      // url: '/ptBacklog/delete'

        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-52', pluginId: 10).save();      // url: '/ptBacklog/showMyBacklog'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-53', pluginId: 10).save();      // url: '/ptBacklog/listMyBacklog'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-54', pluginId: 10).save();      // url: '/ptBacklog/selectMyBacklog'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-55', pluginId: 10).save();      // url: '/ptBacklog/updateMyBacklog'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-56', pluginId: 10).save();      // url: '/ptBacklog/removeMyBacklog'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-57', pluginId: 10).save();      // url: '/ptBacklog/addToMyBacklog'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-39', pluginId: 10).save();      // url: '/ptBacklog/showBackLogForSprint'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-39', pluginId: 10).save();      // url: '/ptBacklog/showBackLogForSprint'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-39', pluginId: 10).save();      // url: '/ptBacklog/showBackLogForSprint'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-46', pluginId: 10).save();      // url: '/ptBacklog/createBackLogForSprint'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-46', pluginId: 10).save();      // url: '/ptBacklog/createBackLogForSprint'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-46', pluginId: 10).save();      // url: '/ptBacklog/createBackLogForSprint'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-47', pluginId: 10).save();      // url: '/ptBacklog/deleteBackLogForSprint'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-47', pluginId: 10).save();      // url: '/ptBacklog/deleteBackLogForSprint'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-47', pluginId: 10).save();      // url: '/ptBacklog/deleteBackLogForSprint'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-48', pluginId: 10).save();      // url: '/ptBacklog/listBackLogForSprint'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-48', pluginId: 10).save();      // url: '/ptBacklog/listBackLogForSprint'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-48', pluginId: 10).save();      // url: '/ptBacklog/listBackLogForSprint'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-71', pluginId: 10).save();      // url: '/ptBacklog/getBacklogList'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-71', pluginId: 10).save();      // url: '/ptBacklog/getBacklogList'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-71', pluginId: 10).save();      // url: '/ptBacklog/getBacklogList'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-83', pluginId: 10).save();      // url: '/ptBacklog/showForActive'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-83', pluginId: 10).save();      // url: '/ptBacklog/showForActive'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-83', pluginId: 10).save();      // url: '/ptBacklog/showForActive'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-84', pluginId: 10).save();      // url: '/ptBacklog/listForActive'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-84', pluginId: 10).save();      // url: '/ptBacklog/listForActive'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-84', pluginId: 10).save();      // url: '/ptBacklog/listForActive'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-85', pluginId: 10).save();      // url: '/ptBacklog/showForInActive'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-85', pluginId: 10).save();      // url: '/ptBacklog/showForInActive'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-85', pluginId: 10).save();      // url: '/ptBacklog/showForInActive'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-86', pluginId: 10).save();      // url: '/ptBacklog/listForInActive'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-86', pluginId: 10).save();      // url: '/ptBacklog/listForInActive'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-86', pluginId: 10).save();      // url: '/ptBacklog/listForInActive'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-94', pluginId: 10).save();      // url: '/ptReport/showForBacklogDetails'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-94', pluginId: 10).save();      // url: '/ptReport/showForBacklogDetails'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-94', pluginId: 10).save();      // url: '/ptReport/showForBacklogDetails'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-95', pluginId: 10).save();      // url: '/ptReport/searchForBacklogDetails'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-95', pluginId: 10).save();      // url: '/ptReport/searchForBacklogDetails'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-95', pluginId: 10).save();      // url: '/ptReport/searchForBacklogDetails'

        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-96', pluginId: 10).save();      // url: '/ptBacklog/acceptStory'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-40', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/show'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-40', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/show'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-41', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/list'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-41', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/list'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-41', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/list'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-42', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/select'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-42', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/select'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-42', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/select'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-43', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/create'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-43', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/create'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-44', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/update'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-44', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/update'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-45', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/delete'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-45', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/delete'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-98', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/showForMyBacklog'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-98', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/showForMyBacklog'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-99', pluginId: 10).save();      // url: '/ptBug/reOpenBug'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-99', pluginId: 10).save();      // url: '/ptBug/reOpenBug'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-100', pluginId: 10).save();      // url: '/ptBug/closeBug'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-100', pluginId: 10).save();      // url: '/ptBug/closeBug'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-102', pluginId: 10).save();      // url: '/ptBug/showBugDetails'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-102', pluginId: 10).save();      // url: '/ptBug/showBugDetails'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-102', pluginId: 10).save();      // url: '/ptBug/showBugDetails'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-117', pluginId: 10).save();      // url: '/ptBug/searchBugDetails'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-117', pluginId: 10).save();      // url: '/ptBug/searchBugDetails'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-117', pluginId: 10).save();      // url: '/ptBug/searchBugDetails'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-104', pluginId: 10).save();      // url: '/ptBug/showOrphanBug'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-104', pluginId: 10).save();      // url: '/ptBug/showOrphanBug'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-104', pluginId: 10).save();      // url: '/ptBug/showOrphanBug'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-105', pluginId: 10).save();      // url: '/ptBug/createOrphanBug'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-105', pluginId: 10).save();      // url: '/ptBug/createOrphanBug'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-105', pluginId: 10).save();      // url: '/ptBug/createOrphanBug'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-106', pluginId: 10).save();      // url: '/ptBug/updateOrphanBug'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-106', pluginId: 10).save();      // url: '/ptBug/updateOrphanBug'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-106', pluginId: 10).save();      // url: '/ptBug/updateOrphanBug'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-107', pluginId: 10).save();      // url: '/ptBug/listOrphanBug'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-107', pluginId: 10).save();      // url: '/ptBug/listOrphanBug'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-107', pluginId: 10).save();      // url: '/ptBug/listOrphanBug'

        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-108', pluginId: 10).save();      // url: '/ptBug/addToMyBug'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-115', pluginId: 10).save();      // url: '/ptBug/showMyBug'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-116', pluginId: 10).save();      // url: '/ptBug/listMyBug'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-72', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/showForMyBacklog'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-72', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/showForMyBacklog'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-72', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/showForMyBacklog'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-73', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/updateForMyBacklog'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-73', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/updateForMyBacklog'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-73', pluginId: 10).save();      // url: '/ptAcceptanceCriteria/updateForMyBacklog'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-33', pluginId: 10).save();      // url: '/ptProject/show'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-34', pluginId: 10).save();      // url: '/ptProject/create'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-35', pluginId: 10).save();      // url: '/ptProject/update'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-36', pluginId: 10).save();      // url: '/ptProject/delete'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-37', pluginId: 10).save();      // url: '/ptProject/select'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-38', pluginId: 10).save();      // url: '/ptProject/list'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-21', pluginId: 10).save();      // url: '/ptModule/show'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-22', pluginId: 10).save();      // url: '/ptModule/create'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-23', pluginId: 10).save();      // url: '/ptModule/select'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-24', pluginId: 10).save();      // url: '/ptModule/update'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-25', pluginId: 10).save();      // url: '/ptModule/delete'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-26', pluginId: 10).save();      // url: '/ptModule/list'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-68', pluginId: 10).save();      // url: '/ptModule/getModuleList'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-27', pluginId: 10).save();      // url: '/ptProjectModule/show'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-28', pluginId: 10).save();      // url: '/ptProjectModule/create'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-29', pluginId: 10).save();      // url: '/ptProjectModule/select'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-30', pluginId: 10).save();      // url: '/ptProjectModule/update'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-31', pluginId: 10).save();      // url: '/ptProjectModule/delete'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-32', pluginId: 10).save();      // url: '/ptProjectModule/list'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-8', pluginId: 10).save();      // url: '/ptBug/show'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-8', pluginId: 10).save();      // url: '/ptBug/show'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-8', pluginId: 10).save();      // url: '/ptBug/show'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-9', pluginId: 10).save();      // url: '/ptBug/create'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-9', pluginId: 10).save();      // url: '/ptBug/create'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-9', pluginId: 10).save();      // url: '/ptBug/create'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-10', pluginId: 10).save();      // url: '/ptBug/update'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-10', pluginId: 10).save();      // url: '/ptBug/update'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-10', pluginId: 10).save();      // url: '/ptBug/update'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-11', pluginId: 10).save();      // url: '/ptBug/delete'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-11', pluginId: 10).save();      // url: '/ptBug/delete'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-11', pluginId: 10).save();      // url: '/ptBug/delete'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-12', pluginId: 10).save();      // url: '/ptBug/select'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-12', pluginId: 10).save();      // url: '/ptBug/select'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-12', pluginId: 10).save();      // url: '/ptBug/select'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-13', pluginId: 10).save();      // url: '/ptBug/list'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-13', pluginId: 10).save();      // url: '/ptBug/list'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-13', pluginId: 10).save();      // url: '/ptBug/list'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-78', pluginId: 10).save();      // url: '/ptBug/showBugForSprint'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-79', pluginId: 10).save();      // url: '/ptBug/createBugForSprint'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-80', pluginId: 10).save();      // url: '/ptBug/deleteBugForSprint'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-81', pluginId: 10).save();      // url: '/ptBug/listBugForSprint'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-75', pluginId: 10).save();      // url: '/ptBug/showBugForMyTask'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-75', pluginId: 10).save();      // url: '/ptBug/showBugForMyTask'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-76', pluginId: 10).save();      // url: '/ptBug/updateBugForMyTask'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-76', pluginId: 10).save();      // url: '/ptBug/updateBugForMyTask'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-77', pluginId: 10).save();      // url: '/ptBug/selectBugForMyTask'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-77', pluginId: 10).save();      // url: '/ptBug/selectBugForMyTask'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-74', pluginId: 10).save();      // url: '/ptBug/downloadBugContent'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-74', pluginId: 10).save();      // url: '/ptBug/downloadBugContent'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-74', pluginId: 10).save();      // url: '/ptBug/downloadBugContent'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-14', pluginId: 10).save();      // url: '/ptSprint/show'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-15', pluginId: 10).save();      // url: '/ptSprint/create'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-16', pluginId: 10).save();      // url: '/ptSprint/update'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-17', pluginId: 10).save();      // url: '/ptSprint/delete'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-18', pluginId: 10).save();      // url: '/ptSprint/list'
        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-20', pluginId: 10).save();      // url: '/ptSprint/select'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-67', pluginId: 10).save();      // url: '/ptSprint/listSprintByProjectId'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-67', pluginId: 10).save();      // url: '/ptSprint/listSprintByProjectId'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-67', pluginId: 10).save();      // url: '/ptSprint/listSprintByProjectId'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-87', pluginId: 10).save();      // url: '/ptSprint/listInActiveSprintByProjectId'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-87', pluginId: 10).save();      // url: '/ptSprint/listInActiveSprintByProjectId'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-87', pluginId: 10).save();      // url: '/ptSprint/listInActiveSprintByProjectId'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-61', pluginId: 10).save();      // url: '/ptReport/showReportOpenBacklog'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-61', pluginId: 10).save();      // url: '/ptReport/showReportOpenBacklog'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-61', pluginId: 10).save();      // url: '/ptReport/showReportOpenBacklog'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-62', pluginId: 10).save();      // url: '/ptReport/downloadOpenBacklogReport'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-62', pluginId: 10).save();      // url: '/ptReport/downloadOpenBacklogReport'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-62', pluginId: 10).save();      // url: '/ptReport/downloadOpenBacklogReport'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-63', pluginId: 10).save();      // url: '/ptReport/listReportOpenBacklog'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-63', pluginId: 10).save();      // url: '/ptReport/listReportOpenBacklog'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-63', pluginId: 10).save();      // url: '/ptReport/listReportOpenBacklog'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-58', pluginId: 10).save();      // url: '/ptReport/downloadSprintDetails'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-58', pluginId: 10).save();      // url: '/ptReport/downloadSprintDetails'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-58', pluginId: 10).save();      // url: '/ptReport/downloadSprintDetails'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-59', pluginId: 10).save();      // url: '/ptReport/showReportSprint'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-59', pluginId: 10).save();      // url: '/ptReport/showReportSprint'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-59', pluginId: 10).save();      // url: '/ptReport/showReportSprint'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-60', pluginId: 10).save();      // url: '/ptReport/listSprintDetails'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-60', pluginId: 10).save();      // url: '/ptReport/listSprintDetails'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-60', pluginId: 10).save();      // url: '/ptReport/listSprintDetails'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-64', pluginId: 10).save();      // url: '/ptReport/downloadBugDetails'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-64', pluginId: 10).save();      // url: '/ptReport/downloadBugDetails'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-64', pluginId: 10).save();      // url: '/ptReport/downloadBugDetails'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-65', pluginId: 10).save();      // url: '/ptReport/showReportBug'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-65', pluginId: 10).save();      // url: '/ptReport/showReportBug'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-65', pluginId: 10).save();      // url: '/ptReport/showReportBug'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-66', pluginId: 10).save();      // url: '/ptReport/listBugDetails'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-66', pluginId: 10).save();      // url: '/ptReport/listBugDetails'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-66', pluginId: 10).save();      // url: '/ptReport/listBugDetails'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-101', pluginId: 10).save();      // url: '/ptReport/downloadBacklogDetailsReport'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-101', pluginId: 10).save();      // url: '/ptReport/downloadBacklogDetailsReport'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-101', pluginId: 10).save();      // url: '/ptReport/downloadBacklogDetailsReport'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-103', pluginId: 10).save();      // url: '/ptReport/downloadPtBugDetails'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-103', pluginId: 10).save();      // url: '/ptReport/downloadPtBugDetails'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-103', pluginId: 10).save();      // url: '/ptReport/downloadPtBugDetails'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-109', pluginId: 10).save();      // url: '/ptFlow/show'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-109', pluginId: 10).save();      // url: '/ptFlow/show'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-109', pluginId: 10).save();      // url: '/ptFlow/show'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-110', pluginId: 10).save();      // url: '/ptFlow/list'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-110', pluginId: 10).save();      // url: '/ptFlow/list'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-110', pluginId: 10).save();      // url: '/ptFlow/list'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-111', pluginId: 10).save();      // url: '/ptFlow/select'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-111', pluginId: 10).save();      // url: '/ptFlow/select'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-111', pluginId: 10).save();      // url: '/ptFlow/select'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-112', pluginId: 10).save();      // url: '/ptFlow/create'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-112', pluginId: 10).save();      // url: '/ptFlow/create'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-112', pluginId: 10).save();      // url: '/ptFlow/create'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-113', pluginId: 10).save();      // url: '/ptFlow/update'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-113', pluginId: 10).save();      // url: '/ptFlow/update'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-113', pluginId: 10).save();      // url: '/ptFlow/update'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-114', pluginId: 10).save();      // url: '/ptFlow/delete'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-114', pluginId: 10).save();      // url: '/ptFlow/delete'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-114', pluginId: 10).save();      // url: '/ptFlow/delete'

        new RoleFeatureMapping(roleTypeId: -16, transactionCode: 'PT-118', pluginId: 10).save();      // url: '/ptBug/bugListForModule'
        new RoleFeatureMapping(roleTypeId: -17, transactionCode: 'PT-118', pluginId: 10).save();      // url: '/ptBug/bugListForModule'
        new RoleFeatureMapping(roleTypeId: -18, transactionCode: 'PT-118', pluginId: 10).save();      // url: '/ptBug/bugListForModule'

        return true
    }

    public boolean createRoleFeatureMapForArmsPlugin(){

        //renderArmsMenu for all user
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-1', pluginId: 11).save();      // url: '/arms/renderArmsMenu'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-1', pluginId: 11).save();      // url: '/arms/renderArmsMenu'
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-1', pluginId: 11).save();      // url: '/arms/renderArmsMenu'
        new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'RMS-1', pluginId: 11).save();      // url: '/arms/renderArmsMenu'
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'RMS-1', pluginId: 11).save();      // url: '/arms/renderArmsMenu'

        //DropDown for all
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-88', pluginId: 11).save();      // url: '/arms/reloadExchangeHouseDropDown'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-88', pluginId: 11).save();      // url: '/arms/reloadExchangeHouseDropDown'
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-88', pluginId: 11).save();      // url: '/arms/reloadExchangeHouseDropDown'
        new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'RMS-88', pluginId: 11).save();      // url: '/arms/reloadExchangeHouseDropDown'
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'RMS-88', pluginId: 11).save();      // url: '/arms/reloadExchangeHouseDropDown'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-89', pluginId: 11).save();      // url: '/arms/reloadExchangeHouseFilteredDropDown'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-89', pluginId: 11).save();      // url: '/arms/reloadExchangeHouseFilteredDropDown'
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-89', pluginId: 11).save();      // url: '/arms/reloadExchangeHouseFilteredDropDown'
        new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'RMS-89', pluginId: 11).save();      // url: '/arms/reloadExchangeHouseFilteredDropDown'
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'RMS-89', pluginId: 11).save();      // url: '/arms/reloadExchangeHouseFilteredDropDown'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-90', pluginId: 11).save();      // url: '/rmsTaskList/reloadTaskListDropDown'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-90', pluginId: 11).save();      //url: '/rmsTaskList/reloadTaskListDropDown'
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-90', pluginId: 11).save();      // url: '/rmsTaskList/reloadTaskListDropDown'
        new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'RMS-90', pluginId: 11).save();      // url: '/rmsTaskList/reloadTaskListDropDown'
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'RMS-90', pluginId: 11).save();      // url: '/rmsTaskList/reloadTaskListDropDown'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-122', pluginId: 11).save();      // url: '/rmsTaskList/reloadTaskListDropDown'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-122', pluginId: 11).save();      //url: '/rmsTaskList/reloadTaskListDropDown'
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-122', pluginId: 11).save();      // url: '/rmsTaskList/reloadTaskListDropDown'
        new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'RMS-122', pluginId: 11).save();      // url: '/rmsTaskList/reloadTaskListDropDown'
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'RMS-122', pluginId: 11).save();      // url: '/rmsTaskList/reloadTaskListDropDown'

        //reload entity note taglib
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-252', pluginId: 11).save();  //url: '/entityNote/reloadEntityNote'
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'APP-252', pluginId: 11).save();  //url: '/entityNote/reloadEntityNote'
        new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-252', pluginId: 11).save();  //url: '/entityNote/reloadEntityNote'
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'APP-252', pluginId: 11).save();  //url: '/entityNote/reloadEntityNote'

        // rms ExchangeHouse CRUD For Admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-2', pluginId: 11).save();      // url: '/rmsExchangeHouse/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-3', pluginId: 11).save();      // url: '/rmsExchangeHouse/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-4', pluginId: 11).save();      // url: '/rmsExchangeHouse/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-5', pluginId: 11).save();      // url: '/rmsExchangeHouse/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-6', pluginId: 11).save();      // url: '/rmsExchangeHouse/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-7', pluginId: 11).save();      // url: '/rmsExchangeHouse/list'

        // rms ExchangeHouseCurrencyPosting for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-10', pluginId: 11).save();      // url: '/rmsExchangeHouseCurrencyPosting/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-11', pluginId: 11).save();      // url: '/rmsExchangeHouseCurrencyPosting/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-12', pluginId: 11).save();      // url: '/rmsExchangeHouseCurrencyPosting/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-13', pluginId: 11).save();      // url: '/rmsExchangeHouseCurrencyPosting/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-14', pluginId: 11).save();      // url: '/rmsExchangeHouseCurrencyPosting/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-15', pluginId: 11).save();      // url: '/rmsExchangeHouseCurrencyPosting/list'

        // rms Process Instrument Mapping for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-16', pluginId: 11).save();      // url: '/rmsProcessInstrumentMapping/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-17', pluginId: 11).save();      // url: '/rmsProcessInstrumentMapping/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-18', pluginId: 11).save();      // url: '/rmsProcessInstrumentMapping/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-19', pluginId: 11).save();      // url: '/rmsProcessInstrumentMapping/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-20', pluginId: 11).save();      // url: '/rmsProcessInstrumentMapping/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-21', pluginId: 11).save();      // url: '/rmsProcessInstrumentMapping/list'

        // rmsTask CRUD For Remittance User
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-23', pluginId: 11).save();      // url: '/rmsTask/show'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-24', pluginId: 11).save();      // url: '/rmsTask/create'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-25', pluginId: 11).save();      // url: '/rmsTask/update'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-26', pluginId: 11).save();      // url: '/rmsTask/delete'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-27', pluginId: 11).save();      // url: '/rmsTask/select'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-28', pluginId: 11).save();      // url: '/rmsTask/list'

        // rmsTask CRUD For admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-23', pluginId: 11).save();      // url: '/rmsTask/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-24', pluginId: 11).save();      // url: '/rmsTask/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-25', pluginId: 11).save();      // url: '/rmsTask/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-26', pluginId: 11).save();      // url: '/rmsTask/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-27', pluginId: 11).save();      // url: '/rmsTask/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-28', pluginId: 11).save();      // url: '/rmsTask/list'

        // rmsTask CRUD For exhHouse User
        new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'RMS-84', pluginId: 11).save();      // url: '/rmsTask/showForExh'
        new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'RMS-24', pluginId: 11).save();      // url: '/rmsTask/create'
        new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'RMS-25', pluginId: 11).save();      // url: '/rmsTask/update'
        new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'RMS-26', pluginId: 11).save();      // url: '/rmsTask/delete'
        new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'RMS-27', pluginId: 11).save();      // url: '/rmsTask/select'
        new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'RMS-28', pluginId: 11).save();      // url: '/rmsTask/list'
        new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'RMS-86', pluginId: 11).save();      // url: '/rmsTask/sendRmsTaskToBank'

        //rmsTask upload for remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-29', pluginId: 11).save();      // url: '/rmsTask/showForUploadTask'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-30', pluginId: 11).save();      // url: '/rmsTask/createForUploadTask'

        //rmsTask upload for exhHouse user
        new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'RMS-85', pluginId: 11).save();      // url: '/rmsTask/showForUploadTaskForExh'
        new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'RMS-30', pluginId: 11).save();      // url: '/rmsTask/createForUploadTask'

        //rmsTask list for remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-31', pluginId: 11).save();      // url: '/rmsTask/listTaskForTaskList'
        //rmsTask list Map for remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-32', pluginId: 11).save();      // url: '/rmsTask/showForMapTask'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-33', pluginId: 11).save();      // url: '/rmsTask/listTaskForMap'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-34', pluginId: 11).save();      // url: '/rmsTask/mapTask'

        //rms task approve for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-35', pluginId: 11).save();      // url: '/rmsTask/showForApproveTask'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-36', pluginId: 11).save();      // url: '/rmsTask/listTaskForApprove'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-37', pluginId: 11).save();      // url: '/rmsTask/approve'

        //rms reviseTask for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-38', pluginId: 11).save();      // url: '/rmsTask/reviseTask'
        //rms reviseTask for remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-38', pluginId: 11).save();      // url: '/rmsTask/reviseTask'
        //rms reviseTask for branch user
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-38', pluginId: 11).save();      // url: '/rmsTask/reviseTask'

        //rmsTask details with note for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-44', pluginId: 11).save();      // url: '/rmsTask/showTaskDetailsWithNote'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-45', pluginId: 11).save();      // url: '/rmsTask/searchTaskDetailsWithNote'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-64', pluginId: 11).save();      // url: '/rmsTask/createRmsTaskNote'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-78', pluginId: 11).save();      //url: '/rmsTask/renderTaskDetails'

        //rmsTask details with note for remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-44', pluginId: 11).save();      // url: '/rmsTask/showTaskDetailsWithNote'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-45', pluginId: 11).save();      // url: '/rmsTask/searchTaskDetailsWithNote'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-64', pluginId: 11).save();      // url: '/rmsTask/createRmsTaskNote'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-78', pluginId: 11).save();      //url: '/rmsTask/renderTaskDetails'

        //rmsTask details with note for branch user
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-44', pluginId: 11).save();      // url: '/rmsTask/showTaskDetailsWithNote'
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-45', pluginId: 11).save();      // url: '/rmsTask/searchTaskDetailsWithNote'
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-64', pluginId: 11).save();      // url: '/rmsTask/createRmsTaskNote'
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-78', pluginId: 11).save();      //url: '/rmsTask/renderTaskDetails'

        //rmsTask details with note for exhHouse user
        new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'RMS-44', pluginId: 11).save();      // url: '/rmsTask/showTaskDetailsWithNote'
        new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'RMS-45', pluginId: 11).save();      // url: '/rmsTask/searchTaskDetailsWithNote'
        new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'RMS-64', pluginId: 11).save();      // url: '/rmsTask/createRmsTaskNote'
        new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'RMS-78', pluginId: 11).save();      //url: '/rmsTask/renderTaskDetails'

        //rmsTask details with note for otherBank user
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'RMS-44', pluginId: 11).save();      // url: '/rmsTask/showTaskDetailsWithNote'
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'RMS-45', pluginId: 11).save();      // url: '/rmsTask/searchTaskDetailsWithNote'
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'RMS-64', pluginId: 11).save();      // url: '/rmsTask/createRmsTaskNote'
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'RMS-78', pluginId: 11).save();      //url: '/rmsTask/renderTaskDetails'

        //disburse rmsTask for remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-79', pluginId: 11).save();      //url: '/rmsTask/disburseRmsTask'
        //disburse rmsTask for branchUser
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-79', pluginId: 11).save();      //url: '/rmsTask/disburseRmsTask'

        //get district For remittance user, branch user, other bank branch user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-236', pluginId: 11).save();      //url: '/rmsTask/reloadDistrictDropDown'
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'APP-236', pluginId: 11).save();      //url: '/rmsTask/reloadDistrictDropDown'
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'APP-236', pluginId: 11).save();      //url: '/rmsTask/reloadDistrictDropDown'

        //get branch For remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-209', pluginId: 11).save();      //url: '/bankBranch/reloadBranchesDropDownByBankAndDistrict'
        //get branch For branch user
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'APP-209', pluginId: 11).save();      //url: '/bankBranch/reloadBranchesDropDownByBankAndDistrict'
        //get branch For other bank user
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'APP-209', pluginId: 11).save();      //url: '/bankBranch/reloadBranchesDropDownByBankAndDistrict'

        //get bank For remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-260', pluginId: 11).save();   //url: '/bank/reloadBankDropDownTagLib'
        //get bank For branch user
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'APP-260', pluginId: 11).save();   //url: '/bank/reloadBankDropDownTagLib'
        //get bank For other bank user
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'APP-260', pluginId: 11).save();   //url: '/bank/reloadBankDropDownTagLib'

        //forward task for branch user
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-71', pluginId: 11).save();      // url: '/rmsTask/showTaskDetailsForForward'
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-72', pluginId: 11).save();      // url: '/rmsTask/searchTaskDetailsForForward'
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-73', pluginId: 11).save();      // url: '/rmsTask/forwardRmsTask'

        //forward task for other bank user
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'RMS-71', pluginId: 11).save();      // url: '/rmsTask/showTaskDetailsForForward'
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'RMS-72', pluginId: 11).save();      // url: '/rmsTask/searchTaskDetailsForForward'
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'RMS-73', pluginId: 11).save();      // url: '/rmsTask/forwardRmsTask'

        // rmsTaskList for remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-39', pluginId: 11).save();      // url: '/rmsTaskList/show'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-40', pluginId: 11).save();      // url: '/rmsTaskList/create'

        //searchTaskList for remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-42', pluginId: 11).save();      // url: '/rmsTaskList/showSearchTaskList'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-43', pluginId: 11).save();      // url: '/rmsTaskList/listSearchTaskList'

        //searchTaskList for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-42', pluginId: 11).save();      // url: '/rmsTaskList/showSearchTaskList'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-43', pluginId: 11).save();      // url: '/rmsTaskList/listSearchTaskList'

        // rmsInstrument for remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-46', pluginId: 11).save();      // url: '/rmsInstrument/listTaskForProcessInstrument'
        // rmsInstrument for branch user
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-46', pluginId: 11).save();      // url: '/rmsInstrument/listTaskForProcessInstrument'
        // rmsInstrument for other bank user
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'RMS-46', pluginId: 11).save();      // url: '/rmsInstrument/listTaskForProcessInstrument'

        //rmsInstrument issue for remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-47', pluginId: 11).save();      // url: '/rmsInstrument/showForIssuePo'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-48', pluginId: 11).save();      // url: '/rmsInstrument/downloadTaskReportForIssuePo'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-49', pluginId: 11).save();      // url: '/rmsInstrument/showForIssueEft'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-50', pluginId: 11).save();      // url: '/rmsInstrument/downloadTaskReportForIssueEft'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-51', pluginId: 11).save();      // url: '/rmsInstrument/showForIssueOnline'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-52', pluginId: 11).save();      // url: '/rmsInstrument/downloadTaskReportForIssueOnline'

        //rmsInstrument issue for branch user
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-55', pluginId: 11).save();      // url: '/rmsInstrument/showForForwardCashCollection'
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-56', pluginId: 11).save();      // url: '/rmsInstrument/downloadTaskReportForForwardCashCollection'
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-57', pluginId: 11).save();      // url: '/rmsInstrument/showForForwardOnline'
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-58', pluginId: 11).save();      // url: '/rmsInstrument/downloadTaskReportForForwardOnline'

        //rmsInstrument issue for other bank user
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'RMS-55', pluginId: 11).save();      // url: '/rmsInstrument/showForForwardCashCollection'
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'RMS-56', pluginId: 11).save();      // url: '/rmsInstrument/downloadTaskReportForForwardCashCollection'

        //rms instrument dropDown for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-120', pluginId: 11).save();      // url: '/rmsInstrument/reloadInstrumentDropDown'
        //rms instrument dropDown for remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-121', pluginId: 11).save();      // url: '/rmsInstrument/reloadBankListFilteredDropDown'
        //rms instrument dropDown for remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-120', pluginId: 11).save();      // url: '/rmsInstrument/reloadInstrumentDropDown'

        //purchase instrument for remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-118', pluginId: 11).save();      // url: '/rmsInstrument/showForInstrumentPurchase'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-119', pluginId: 11).save();      // url: '/rmsInstrument/downloadTaskReportForPurchaseInstrument'

        // rmsReport for remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-59', pluginId: 11).save();      // url: '/rmsReport/showForListWiseStatusReport'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-60', pluginId: 11).save();      // url: '/rmsReport/listForListWiseStatusReport'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-61', pluginId: 11).save();      // url: '/rmsReport/downloadListWiseStatusReport'

        // rmsReport for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-59', pluginId: 11).save();      // url: '/rmsReport/showForListWiseStatusReport'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-60', pluginId: 11).save();      // url: '/rmsReport/listForListWiseStatusReport'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-61', pluginId: 11).save();      // url: '/rmsReport/downloadListWiseStatusReport'

        //search beneficiary for remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-62', pluginId: 11).save();      // url: '/rmsReport/showBeneficiaryDetails'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-63', pluginId: 11).save();      // url: '/rmsReport/searchBeneficiaryDetails'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-83', pluginId: 11).save();      // url: '/rmsReport/searchBeneficiaryForGrid'

        //search beneficiary for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-62', pluginId: 11).save();      // url: '/rmsReport/showBeneficiaryDetails'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-63', pluginId: 11).save();      // url: '/rmsReport/searchBeneficiaryDetails'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-83', pluginId: 11).save();      // url: '/rmsReport/searchBeneficiaryForGrid'

        //search task list plan for remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-65', pluginId: 11).save();      // url: '/rmsReport/showTaskListPlan'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-66', pluginId: 11).save();      // url: '/rmsReport/searchTaskListPlan'

        //search task list plan for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-65', pluginId: 11).save();      // url: '/rmsReport/showTaskListPlan'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-66', pluginId: 11).save();      // url: '/rmsReport/searchTaskListPlan'

        //forwarded unpaid task for remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-74', pluginId: 11).save();      //url: '/rmsTask/showForForwardUnpaidTask'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-75', pluginId: 11).save();      //url: '/rmsTask/listTaskForForwardUnpaidTask'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-126', pluginId: 11).save();     //url: '/rmsReport/listTaskDetailsForForwardedUnpaidTasks'

        //forwarded unpaid task for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-74', pluginId: 11).save();      //url: '/rmsTask/showForForwardUnpaidTask'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-75', pluginId: 11).save();      //url: '/rmsTask/listTaskForForwardUnpaidTask'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-126', pluginId: 11).save();     //url: '/rmsReport/listTaskDetailsForForwardedUnpaidTasks'

        //taskTrace for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-76', pluginId: 11).save();      //url: '/rmsTaskTrace/showRmsTaskHistory'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-77', pluginId: 11).save();      //url: '/rmsTaskTrace/searchRmsTaskHistory'
        //taskTrace for remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-76', pluginId: 11).save();      //url: '/rmsTaskTrace/showRmsTaskHistory'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-77', pluginId: 11).save();      //url: '/rmsTaskTrace/searchRmsTaskHistory'

        //disburse cash collection for branch user
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-80', pluginId: 11).save();      //url: '/rmsTask/showDisburseCashCollection'
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-81', pluginId: 11).save();      //url: '/rmsTask/searchDisburseCashCollection'
        new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'RMS-82', pluginId: 11).save();      //url: '/rmsTask/disburseCashCollectionRmsTask'

        //disburse cash collection for other bank user
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'RMS-80', pluginId: 11).save();      //url: '/rmsTask/showDisburseCashCollection'
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'RMS-81', pluginId: 11).save();      //url: '/rmsTask/searchDisburseCashCollection'
        new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'RMS-82', pluginId: 11).save();      //url: '/rmsTask/disburseCashCollectionRmsTask'

        //manage task for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-91', pluginId: 11).save();      //url: '/rmsTask/showForManageTask'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-106', pluginId: 11).save();      //url: '/rmsTask/cancelRmsTask'
        //cancel task for remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-106', pluginId: 11).save();      //url: '/rmsTask/cancelRmsTask'

        //manage task list for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-93', pluginId: 11).save();      //url: '/rmsTaskList/showForManageTaskList'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-94', pluginId: 11).save();      //url: '/rmsTaskList/listForManageTaskList'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-105', pluginId: 11).save();      //url: '/rmsTasklist/removeFromList'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-108', pluginId: 11).save();      //url: '/rmsTaskList/renameTaskList'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-109', pluginId: 11).save();      //url: '/rmsTaskList/moveTaskToAnotherList'

        //RmsTransactionDay for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-95', pluginId: 11).save();      //url: '/rmsTransactionDay/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-96', pluginId: 11).save();      //url: '/rmsTransactionDay/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-97', pluginId: 11).save();      //url: '/rmsTransactionDay/openTransactionDay'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-98', pluginId: 11).save();      //url: '/rmsTransactionDay/closeTransactionDay'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-99', pluginId: 11).save();      //url: '/rmsTransactionDay/reOpenTransactionDay'

        //RmsTaskListSummaryModel for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-107', pluginId: 11).save();      //url: '/rmsTaskListSummaryModel/listUnResolvedTaskList'

        ////RmsPurchaseInstrumentMapping for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-110', pluginId: 11).save();      // url: '/rmsPurchaseInstrumentMapping/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-111', pluginId: 11).save();      // url: '/rmsPurchaseInstrumentMapping/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-112', pluginId: 11).save();      // url: '/rmsPurchaseInstrumentMapping/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-113', pluginId: 11).save();      // url: '/rmsPurchaseInstrumentMapping/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-114', pluginId: 11).save();      // url: '/rmsPurchaseInstrumentMapping/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-115', pluginId: 11).save();      // url: '/rmsPurchaseInstrumentMapping/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-125', pluginId: 11).save();      // url: '/rmsPurchaseInstrumentMapping/evaluateLogic'

        //rmsViewNotes for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-116', pluginId: 11).save();      // url: '/rmsTask/showForViewNotes'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-117', pluginId: 11).save();      // url: '/rmsTask/listForViewNotes'
        //view cancelled tasks
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-123', pluginId: 11).save();      // url: '/rmsReport/showForViewCancelTask'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-124', pluginId: 11).save();      // url: '/rmsReport/listForViewCancelTask'

        //decisionSummary for admin
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-127', pluginId: 11).save();      // url: '/rmsReport/showDecisionSummary'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-128', pluginId: 11).save();      // url: '/rmsReport/listDecisionSummary'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'RMS-129', pluginId: 11).save();      // url: '/rmsReport/downloadDecisionSummaryReport'

        //decisionSummary for remittance user
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-127', pluginId: 11).save();      // url: '/rmsReport/showDecisionSummary'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-128', pluginId: 11).save();      // url: '/rmsReport/listDecisionSummary'
        new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'RMS-129', pluginId: 11).save();      // url: '/rmsReport/downloadDecisionSummaryReport'

        return true
    }

    public boolean createRoleFeatureMapForSARBPlugin(){

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-1', pluginId: 12).save();   // url: '/sarb/renderSarbMenu'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-2', pluginId: 12).save();   // url: '/sarbProvince/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-3', pluginId: 12).save();   // url: '/sarbProvince/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-4', pluginId: 12).save();   // url: '/sarbProvince/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-5', pluginId: 12).save();   // url: '/sarbProvince/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-6', pluginId: 12).save();   // url: '/sarbProvince/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-7', pluginId: 12).save();   // url: '/sarbProvince/list'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-8', pluginId: 12).save();   // url: '/sarbTaskModel/showForSendTaskToSarb'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-9', pluginId: 12).save();   // url: '/sarbTaskModel/listForSendTaskToSarb'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-13', pluginId: 12).save();   // url: '/sarbTaskModel/sendTaskToSarb'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-10', pluginId: 12).save();   // url: '/sarbTaskModel/showTaskStatus'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-11', pluginId: 12).save();   // url: '/sarbTaskModel/listTaskStatus'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-14', pluginId: 12).save();   // url: '/sarbTaskModel/showTaskStatusDetails'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-15', pluginId: 12).save();  // url: '/sarbTaskModel/showTaskForRetrieveResponse'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-16', pluginId: 12).save();   //url: '/sarbTaskModel/sendToRetrieveResponse'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-12', pluginId: 12).save();   // url: '/sarbTaskModel/listSarbTaskForRetrieveResponse'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-17', pluginId: 12).save();   // url: '/sarbTaskModel/retrieveResponseAgain'

        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'SARB-18', pluginId: 12).save();   //  url: '/sarbTaskModel/moveForResend'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'SARB-19', pluginId: 12).save();   //  url: '/sarbTaskModel/moveForCancel'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'SARB-34', pluginId: 12).save();   //  url: '/sarbTaskModel/moveForReplace'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'SARB-35', pluginId: 12).save();   //  url: '/sarbTaskModel/moveForRefund'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'SARB-20', pluginId: 12).save();   //  url: '/sarbTaskModel/sendCancelTaskToSarb'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'SARB-36', pluginId: 12).save();   //  url: '/sarbTaskModel/sendReplaceTaskToSarb'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'SARB-37', pluginId: 12).save();   //  url: '/sarbTaskModel/sendRefundTaskToSarb'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-21', pluginId: 12).save();   //  url: '/sarbReport/showSarbTransactionSummary'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-22', pluginId: 12).save();   //  url: '/sarbReport/listSarbTransactionSummary'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-23', pluginId: 12).save();   //  url: '/sarbReport/downloadSarbTransactionSummary'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-24', pluginId: 12).save();   //  url: '/sarbTaskModel/listForCancelTask'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-25', pluginId: 12).save();   // url: '/sarbTaskModel/showTaskForCancel'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-26', pluginId: 12).save();   //  url: '/sarbTaskModel/listForReplaceTask'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-27', pluginId: 12).save();   // url: '/sarbTaskModel/showForReplaceTask'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-28', pluginId: 12).save();   //  url: '/sarbTaskModel/listForRefundTask'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'SARB-29', pluginId: 12).save();   // url: '/sarbTaskModel/showForRefundTask'

        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'SARB-32', pluginId: 12).save();   // url: '/sarbTaskModel/updateTaskForReplaceTask'

        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'SARB-30', pluginId: 12).save();   //  url: '/sarbTaskModel/showDetailsForRefundTask'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'SARB-31', pluginId: 12).save();   //  url: '/sarbTaskModel/createSarbTaskForRefundTask'
        new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'SARB-33', pluginId: 12).save();   //  url: '/sarbTaskModel/listRefundTaskForShowStatus'
    }


    public boolean createRoleFeatureMapForDocumentPlugin(){

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-1', pluginId: 13).save();   // url: '/document/renderDocumentMenu'
        new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'DOC-1', pluginId: 13).save();   // url: '/document/renderDocumentMenu'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-2', pluginId: 13).save();   // url: '/docCategory/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-3', pluginId: 13).save();   // url: '/docCategory/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-4', pluginId: 13).save();   // url: '/docCategory/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-5', pluginId: 13).save();   // url: '/docCategory/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-6', pluginId: 13).save();   // url: '/docCategory/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-7', pluginId: 13).save();   // url: '/docCategory/select'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-8', pluginId: 13).save();   // url: '/docSubCategory/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-9', pluginId: 13).save();   // url: '/docSubCategory/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-10', pluginId: 13).save();   // url: '/docSubCategory/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-11', pluginId: 13).save();   // url: '/docSubCategory/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-12', pluginId: 13).save();   // url: '/docSubCategory/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-13', pluginId: 13).save();   // url: '/docSubCategory/select'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-14', pluginId: 13).save();   // url: '/docCategory/showMyCategories'
        new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'DOC-14', pluginId: 13).save();   // url: '/docCategory/showMyCategories'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-15', pluginId: 13).save();   // url: '/docAllCategoryUserMapping/showForCategory'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-16', pluginId: 13).save();   // url: '/docAllCategoryUserMapping/listForCategory'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-17', pluginId: 13).save();   // url: '/docAllCategoryUserMapping/createForCategory'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-18', pluginId: 13).save();   // url: '/docAllCategoryUserMapping/updateForCategory'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-19', pluginId: 13).save();   // url: '/docAllCategoryUserMapping/deleteForCategory'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-20', pluginId: 13).save();   // url: '/docAllCategoryUserMapping/selectForCategory'


        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-21', pluginId: 13).save();   // url: '/docAllCategoryUserMapping/showForSubCategory'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-22', pluginId: 13).save();   // url: '/docAllCategoryUserMapping/listForSubCategory'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-23', pluginId: 13).save();   // url: '/docAllCategoryUserMapping/createForSubCategory'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-24', pluginId: 13).save();   // url: '/docAllCategoryUserMapping/updateForSubCategory'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-25', pluginId: 13).save();   // url: '/docAllCategoryUserMapping/deleteForSubCategory'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-26', pluginId: 13).save();   // url: '/docAllCategoryUserMapping/selectForSubCategory'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-27', pluginId: 13).save();   // url: '/docSubCategory/showMySubCategories'
        new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'DOC-27', pluginId: 13).save();   // url: '/docSubCategory/showMySubCategories'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-28', pluginId: 13).save();   // url: '/docCategory/viewMyCategoryDetails'
        new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'DOC-28', pluginId: 13).save();   // url: '/docCategory/viewMyCategoryDetails'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-29', pluginId: 13).save();   // url: '/docSubCategory/viewMySubCategoryDetails'
        new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'DOC-29', pluginId: 13).save();   // url: '/docSubCategory/viewMySubCategoryDetails'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-30', pluginId: 13).save();   // url: '/docAllCategoryUserMapping/dropDownAppUserForCategoryReload'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-31', pluginId: 13).save();   // url: '/docAllCategoryUserMapping/dropDownAppUserForSubCategoryReload'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-32', pluginId: 13).save();   // url: '/docDbInstance/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-33', pluginId: 13).save();   // url: '/docDbInstance/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-34', pluginId: 13).save();   // url: '/docDbInstance/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-35', pluginId: 13).save();   // url: '/docDbInstance/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-36', pluginId: 13).save();   // url: '/docDbInstance/delete'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-37', pluginId: 13).save();   // url: '/docDbInstance/select'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-38', pluginId: 13).save();   // url: '/docInvitedMembers/showResendInvitation'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-39', pluginId: 13).save();   // url: '/docSubCategory/uploadDocSubCategoryDocument'
        new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'DOC-39', pluginId: 13).save();   // url: '/docSubCategory/uploadDocSubCategoryDocument'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-41', pluginId: 13).save();   // url: '/docInvitedMembers/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-42', pluginId: 13).save();   // url: '/docInvitedMembers/sendInvitation'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-45', pluginId: 13).save();   // url: '/docDbInstance/showResult'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-46', pluginId: 13).save();   // url: '/docDbInstance/listResult'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-47', pluginId: 13).save();   // url: '/docDbInstance/downloadResultCsv'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-48', pluginId: 13).save();   // url: '/docInvitedMembers/outStandingInvitations'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-49', pluginId: 13).save();   // url: '/docInvitedMembers/showOutStandingInvitations'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-50', pluginId: 13).save();   // url: '/docMemberJoinRequest/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-52', pluginId: 13).save();   // url: '/docMemberJoinRequest/approvedForMembership'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-53', pluginId: 13).save();   // url: '/docMemberJoinRequest/searchRequestedMembers'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-54', pluginId: 13).save();   // url: '/docSubCategory/dropDownSubCategoryReload'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-57', pluginId: 13).save();   // url: '/docArticle/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-58', pluginId: 13).save();   // url: '/docArticle/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-59', pluginId: 13).save();   // url: '/docArticle/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-60', pluginId: 13).save();   // url: '/docArticle/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-61', pluginId: 13).save();   // url: '/docArticle/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-62', pluginId: 13).save();   // url: '/docArticle/movedToTrash'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-63', pluginId: 13).save();   // url: '/docArticle/showTrash'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-64', pluginId: 13).save();   // url: '/docArticle/listTrash'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-65', pluginId: 13).save();   // url: '/docArticle/restoreFromTrash'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-66', pluginId: 13).save();   // url: '/docArticle/delete'

        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-67', pluginId: 13).save();   // url: '/docArticleQuery/show'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-68', pluginId: 13).save();   // url: '/docArticleQuery/list'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-69', pluginId: 13).save();   // url: '/docArticleQuery/create'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-70', pluginId: 13).save();   // url: '/docArticleQuery/select'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-71', pluginId: 13).save();   // url: '/docArticleQuery/update'
        new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'DOC-72', pluginId: 13).save();   // url: '/docArticleQuery/delete'
    }

}
